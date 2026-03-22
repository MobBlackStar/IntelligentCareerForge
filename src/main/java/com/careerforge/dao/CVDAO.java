package com.careerforge.dao;

import com.careerforge.model.CV;
import com.careerforge.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * // FEDI: The absolute core of the Phantom ATS Sandbox.
 * // This DAO saves your Master CV, your Mutated Clones, AND the AI Ghost competitors.
 */
public class CVDAO implements ICrud<CV> {

    private Connection connection = DatabaseConnection.getInstance().getConnection();

    @Override
    public void create(CV cv) {
        String query = """
            INSERT INTO cvs (user_id, job_offer_id, is_master, is_ghost, ghost_name, raw_content, ats_score) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            /*
             * FEYNMAN COMMENT: The 'Null' Trap.
             * A real User has an ID (e.g., 1). But a Ghost is fake! It has no user_id.
             * If we try to push a Java 'null' directly into MySQL using .setInt(), it crashes.
             * We must politely tell MySQL: "If the Integer is null, put an actual SQL NULL in the box."
             */
            if (cv.getUserId() == null) {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(1, cv.getUserId());
            }

            if (cv.getJobOfferId() == null) {
                preparedStatement.setNull(2, java.sql.Types.INTEGER);
            } else {
                preparedStatement.setInt(2, cv.getJobOfferId());
            }

            preparedStatement.setBoolean(3, cv.isMaster());
            preparedStatement.setBoolean(4, cv.isGhost());
            preparedStatement.setString(5, cv.getGhostName());
            preparedStatement.setString(6, cv.getRawContent());
            preparedStatement.setInt(7, cv.getAtsScore());

            preparedStatement.executeUpdate();
            IO.println("✅ CV Forged in Database: " + (cv.isGhost() ? cv.getGhostName() : "Real Candidate"));

        } catch (SQLException e) {
            IO.println("❌ Error forging CV: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<CV> readAll() {
        ArrayList<CV> cvList = new ArrayList<>();
        String query = "SELECT * FROM cvs";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                cvList.add(extractCVFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            IO.println("❌ Error reading CVs: " + e.getMessage());
        }
        return cvList;
    }

    /**
     * // SARAH: Use this method in the PhantomSandboxController!
     * // Pass the JobOffer ID, and it will return the 3 AI Ghosts + Fedi's mutated clone for the Leaderboard.
     */
    public ArrayList<CV> getLeaderboardForJob(int jobOfferId) {
        ArrayList<CV> leaderboard = new ArrayList<>();
        // ORDER BY ats_score DESC means the highest score is at the top of the list! (SQL does the sorting for us)
        String query = "SELECT * FROM cvs WHERE job_offer_id = ? ORDER BY ats_score DESC";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, jobOfferId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                leaderboard.add(extractCVFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            IO.println("❌ Error fetching Leaderboard: " + e.getMessage());
        }
        return leaderboard;
    }

    @Override
    public void update(CV cv) {
        String query = """
            UPDATE cvs 
            SET raw_content = ?, ats_score = ? 
            WHERE id = ?
        """;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, cv.getRawContent());
            preparedStatement.setInt(2, cv.getAtsScore());
            preparedStatement.setInt(3, cv.getId());
            preparedStatement.executeUpdate();
            IO.println("✅ CV Updated. New ATS Score: " + cv.getAtsScore());
        } catch (SQLException e) {
            IO.println("❌ Error updating CV: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM cvs WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            IO.println("✅ CV Deleted. ID: " + id);
        } catch (SQLException e) {
            IO.println("❌ Error deleting CV: " + e.getMessage());
        }
    }

    // Helper method to keep code DRY (Don't Repeat Yourself)
    private CV extractCVFromResultSet(ResultSet rs) throws SQLException {
        // We use (Integer) rs.getObject() because rs.getInt() returns 0 instead of null if the DB field is NULL.
        return new CV(
                rs.getInt("id"),
                (Integer) rs.getObject("user_id"),
                (Integer) rs.getObject("job_offer_id"),
                rs.getBoolean("is_master"),
                rs.getBoolean("is_ghost"),
                rs.getString("ghost_name"),
                rs.getString("raw_content"),
                rs.getInt("ats_score")
        );
    }
}
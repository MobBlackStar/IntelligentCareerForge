package com.careerforge.dao;

import com.careerforge.model.Application;
import com.careerforge.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * // FEDI: The engine behind the drag-and-drop War Room.
 * // This saves the 1-Click Arsenal (Cover Letters, Action Plans, DMs).
 */
public class ApplicationDAO implements ICrud<Application> {

    private Connection connection = DatabaseConnection.getInstance().getConnection();

    @Override
    public void create(Application app) {
        String query = """
            INSERT INTO applications (user_id, job_offer_id, cv_id, kanban_status, cover_letter, action_plan, cold_dm) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, app.getUserId());
            preparedStatement.setInt(2, app.getJobOfferId());
            preparedStatement.setInt(3, app.getCvId());
            preparedStatement.setString(4, app.getKanbanStatus());
            preparedStatement.setString(5, app.getCoverLetter());
            preparedStatement.setString(6, app.getActionPlan());
            preparedStatement.setString(7, app.getColdDm());

            preparedStatement.executeUpdate();
            IO.println("✅ Kanban Card Created in status: " + app.getKanbanStatus());
        } catch (SQLException e) {
            IO.println("❌ Error creating Application: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Application> readAll() {
        ArrayList<Application> apps = new ArrayList<>();
        String query = "SELECT * FROM applications";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                apps.add(extractAppFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            IO.println("❌ Error reading Applications: " + e.getMessage());
        }
        return apps;
    }

    /**
     * // SARAH: When the User drops a card into a new column, call this method!
     * // It updates ONLY the status in the database (lightning fast).
     */
    public void updateKanbanStatus(int applicationId, String newStatus) {
        String query = "UPDATE applications SET kanban_status = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, applicationId);
            preparedStatement.executeUpdate();
            IO.println("✅ Kanban Card moved to: " + newStatus);
        } catch (SQLException e) {
            IO.println("❌ Error moving Kanban Card: " + e.getMessage());
        }
    }

    @Override
    public void update(Application app) {
        String query = """
            UPDATE applications 
            SET kanban_status = ?, cover_letter = ?, action_plan = ?, cold_dm = ? 
            WHERE id = ?
        """;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, app.getKanbanStatus());
            preparedStatement.setString(2, app.getCoverLetter());
            preparedStatement.setString(3, app.getActionPlan());
            preparedStatement.setString(4, app.getColdDm());
            preparedStatement.setInt(5, app.getId());
            preparedStatement.executeUpdate();
            IO.println("✅ Arsenal Updated for Application ID: " + app.getId());
        } catch (SQLException e) {
            IO.println("❌ Error updating Application: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM applications WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            IO.println("✅ Application Card Deleted.");
        } catch (SQLException e) {
            IO.println("❌ Error deleting Application: " + e.getMessage());
        }
    }

    private Application extractAppFromResultSet(ResultSet rs) throws SQLException {
        return new Application(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("job_offer_id"),
                rs.getInt("cv_id"),
                rs.getString("kanban_status"),
                rs.getString("cover_letter"),
                rs.getString("action_plan"),
                rs.getString("cold_dm")
        );
    }
}
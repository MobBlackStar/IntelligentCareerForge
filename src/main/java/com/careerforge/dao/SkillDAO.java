package com.careerforge.dao;

import com.careerforge.model.Skill;
import com.careerforge.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * // FEDI: This DAO manages the RPG Skill Tree.
 * // It dictates whether a node glows Blue (Acquired) or Red (Missing).
 */
public class SkillDAO implements ICrud<Skill> {

    private Connection connection = DatabaseConnection.getInstance().getConnection();

    @Override
    public void create(Skill skill) {
        String query = """
            INSERT INTO skills (user_id, skill_name, is_acquired) 
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, skill.getUserId());
            preparedStatement.setString(2, skill.getSkillName());
            preparedStatement.setBoolean(3, skill.isAcquired()); // JDBC handles boolean to TINYINT(1) automatically

            preparedStatement.executeUpdate();
            IO.println("✅ Skill added to Tree: " + skill.getSkillName());
        } catch (SQLException e) {
            IO.println("❌ Error saving Skill: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Skill> readAll() {
        // FEYNMAN COMMENT: We rarely use readAll() for skills because we usually want skills for a SPECIFIC user.
        // But we must include it because our ICrud interface forces us to (Contract Rules!).
        ArrayList<Skill> skills = new ArrayList<>();
        String query = "SELECT * FROM skills";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                skills.add(new Skill(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getBoolean("is_acquired")
                ));
            }
        } catch (SQLException e) {
            IO.println("❌ Error reading Skills: " + e.getMessage());
        }
        return skills;
    }

    /**
     * // SARAH: Use this method in the SkillTreeController!
     * // Pass the logged-in User's ID, and it gives you only THEIR skills to draw on the canvas.
     */
    public ArrayList<Skill> getSkillsByUserId(int userId) {
        ArrayList<Skill> userSkills = new ArrayList<>();
        String query = "SELECT * FROM skills WHERE user_id = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userSkills.add(new Skill(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("skill_name"),
                        resultSet.getBoolean("is_acquired")
                ));
            }
        } catch (SQLException e) {
            IO.println("❌ Error reading User's Skills: " + e.getMessage());
        }
        return userSkills;
    }

    @Override
    public void update(Skill skill) {
        // This is triggered when the AI determines Fedi has finally learned a missing skill!
        String query = """
            UPDATE skills 
            SET skill_name = ?, is_acquired = ? 
            WHERE id = ?
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, skill.getSkillName());
            preparedStatement.setBoolean(2, skill.isAcquired());
            preparedStatement.setInt(3, skill.getId());

            preparedStatement.executeUpdate();
            IO.println("✅ Skill Updated (Leveled Up!): " + skill.getSkillName());
        } catch (SQLException e) {
            IO.println("❌ Error updating Skill: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM skills WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            IO.println("✅ Skill removed from Tree. ID: " + id);
        } catch (SQLException e) {
            IO.println("❌ Error deleting Skill: " + e.getMessage());
        }
    }
}
package com.careerforge.dao;

import com.careerforge.model.JobOffer;
import com.careerforge.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * // FEDI: This DAO handles the "Magic Paste" jobs.
 * // It saves the raw job description and the hidden "Pain Points" the AI diagnoses.
 */
public class JobOfferDAO implements ICrud<JobOffer> {

    private Connection connection = DatabaseConnection.getInstance().getConnection();

    @Override
    public void create(JobOffer jobOffer) {
        String query = """
            INSERT INTO job_offers (title, company, raw_description, company_pain_point) 
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, jobOffer.getTitle());
            preparedStatement.setString(2, jobOffer.getCompany());
            preparedStatement.setString(3, jobOffer.getRawDescription());
            preparedStatement.setString(4, jobOffer.getCompanyPainPoint());

            preparedStatement.executeUpdate();
            IO.println("✅ Magic Paste Saved: " + jobOffer.getTitle());
        } catch (SQLException e) {
            IO.println("❌ Error saving Job Offer: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<JobOffer> readAll() {
        ArrayList<JobOffer> jobOffers = new ArrayList<>();
        String query = """
            SELECT * FROM job_offers
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                JobOffer job = new JobOffer(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("company"),
                        resultSet.getString("raw_description"),
                        resultSet.getString("company_pain_point")
                );
                jobOffers.add(job);
            }
        } catch (SQLException e) {
            IO.println("❌ Error reading Job Offers: " + e.getMessage());
        }
        return jobOffers;
    }

    @Override
    public void update(JobOffer jobOffer) {
        String query = """
            UPDATE job_offers 
            SET title = ?, company = ?, raw_description = ?, company_pain_point = ? 
            WHERE id = ?
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, jobOffer.getTitle());
            preparedStatement.setString(2, jobOffer.getCompany());
            preparedStatement.setString(3, jobOffer.getRawDescription());
            preparedStatement.setString(4, jobOffer.getCompanyPainPoint());
            preparedStatement.setInt(5, jobOffer.getId());

            preparedStatement.executeUpdate();
            IO.println("✅ Job Offer Updated: " + jobOffer.getTitle());
        } catch (SQLException e) {
            IO.println("❌ Error updating Job Offer: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = """
            DELETE FROM job_offers WHERE id = ?
        """;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            IO.println("✅ Job Offer Deleted. ID: " + id);
        } catch (SQLException e) {
            IO.println("❌ Error deleting Job Offer: " + e.getMessage());
        }
    }
}
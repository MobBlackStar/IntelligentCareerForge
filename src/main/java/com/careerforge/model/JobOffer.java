package com.careerforge.model;

/**
 * // FEDI: Represents a job scraped via the "Magic Paste" feature.
 */
public class JobOffer {
    private int id;
    private String title;
    private String company;
    private String rawDescription;
    private String companyPainPoint; // For the Trojan Horse feature

    // Constructor without ID (New from API)
    public JobOffer(String title, String company, String rawDescription, String companyPainPoint) {
        this.title = title;
        this.company = company;
        this.rawDescription = rawDescription;
        this.companyPainPoint = companyPainPoint;
    }

    // Constructor with ID (From Database)
    public JobOffer(int id, String title, String company, String rawDescription, String companyPainPoint) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.rawDescription = rawDescription;
        this.companyPainPoint = companyPainPoint;
    }

    // GETTERS & SETTERS
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return this.company; }
    public void setCompany(String company) { this.company = company; }

    public String getRawDescription() { return this.rawDescription; }
    public void setRawDescription(String rawDescription) { this.rawDescription = rawDescription; }

    public String getCompanyPainPoint() { return this.companyPainPoint; }
    public void setCompanyPainPoint(String companyPainPoint) { this.companyPainPoint = companyPainPoint; }

    @Override
    public String toString() {
        return this.title + " @ " + this.company;
    }
}
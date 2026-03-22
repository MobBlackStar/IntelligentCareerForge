package com.careerforge.model;

/**
 * // FEDI: Represents a card on the War Room Kanban Board.
 */
public class Application {
    private int id;
    private int userId;
    private int jobOfferId;
    private int cvId; // The mutated CV for this job

    private String kanbanStatus; // 'TARGETED', 'APPLIED', 'INTERVIEWING', 'OFFER'
    private String coverLetter;
    private String actionPlan; // The Trojan Horse Document
    private String coldDm;

    // Standard Constructors
    public Application(int userId, int jobOfferId, int cvId, String kanbanStatus, String coverLetter, String actionPlan, String coldDm) {
        this.userId = userId;
        this.jobOfferId = jobOfferId;
        this.cvId = cvId;
        this.kanbanStatus = kanbanStatus;
        this.coverLetter = coverLetter;
        this.actionPlan = actionPlan;
        this.coldDm = coldDm;
    }

    public Application(int id, int userId, int jobOfferId, int cvId, String kanbanStatus, String coverLetter, String actionPlan, String coldDm) {
        this.id = id;
        this.userId = userId;
        this.jobOfferId = jobOfferId;
        this.cvId = cvId;
        this.kanbanStatus = kanbanStatus;
        this.coverLetter = coverLetter;
        this.actionPlan = actionPlan;
        this.coldDm = coldDm;
    }

    // GETTERS & SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getJobOfferId() { return jobOfferId; }
    public void setJobOfferId(int jobOfferId) { this.jobOfferId = jobOfferId; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public String getKanbanStatus() { return kanbanStatus; }
    public void setKanbanStatus(String kanbanStatus) { this.kanbanStatus = kanbanStatus; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public String getActionPlan() { return actionPlan; }
    public void setActionPlan(String actionPlan) { this.actionPlan = actionPlan; }
    public String getColdDm() { return coldDm; }
    public void setColdDm(String coldDm) { this.coldDm = coldDm; }

    @Override
    public String toString() {
        return "Application [" + this.kanbanStatus + "]";
    }
}
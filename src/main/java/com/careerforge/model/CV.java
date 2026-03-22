package com.careerforge.model;

/**
 * // FEDI: Handles Fedi's Master CV, Targeted Clones, AND the Fake Ghost CVs.
 */
public class CV {
    private int id;
    // FEYNMAN COMMENT: Why Integer (Object) instead of int (primitive)?
    // Because a Ghost CV doesn't belong to a real User! So userId can be NULL. Primitives cannot be null.
    private Integer userId;
    private Integer jobOfferId;

    private boolean isMaster;
    private boolean isGhost;
    private String ghostName; // E.g., "Nour (Ghost)"
    private String rawContent; // The Fedi-Standard Markdown
    private int atsScore; // For the Live Leaderboard

    // New CV
    public CV(Integer userId, Integer jobOfferId, boolean isMaster, boolean isGhost, String ghostName, String rawContent, int atsScore) {
        this.userId = userId;
        this.jobOfferId = jobOfferId;
        this.isMaster = isMaster;
        this.isGhost = isGhost;
        this.ghostName = ghostName;
        this.rawContent = rawContent;
        this.atsScore = atsScore;
    }

    // DB CV
    public CV(int id, Integer userId, Integer jobOfferId, boolean isMaster, boolean isGhost, String ghostName, String rawContent, int atsScore) {
        this.id = id;
        this.userId = userId;
        this.jobOfferId = jobOfferId;
        this.isMaster = isMaster;
        this.isGhost = isGhost;
        this.ghostName = ghostName;
        this.rawContent = rawContent;
        this.atsScore = atsScore;
    }

    // GETTERS & SETTERS (Self-evident for POJO)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getJobOfferId() { return jobOfferId; }
    public void setJobOfferId(Integer jobOfferId) { this.jobOfferId = jobOfferId; }
    public boolean isMaster() { return isMaster; }
    public void setMaster(boolean master) { isMaster = master; }
    public boolean isGhost() { return isGhost; }
    public void setGhost(boolean ghost) { isGhost = ghost; }
    public String getGhostName() { return ghostName; }
    public void setGhostName(String ghostName) { this.ghostName = ghostName; }
    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
    public int getAtsScore() { return atsScore; }
    public void setAtsScore(int atsScore) { this.atsScore = atsScore; }

    @Override
    public String toString() {
        return (this.isGhost ? "👻 " + this.ghostName : "📄 CV") + " [Score ATS: " + this.atsScore + "]";
    }
}
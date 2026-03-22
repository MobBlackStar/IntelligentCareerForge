package com.careerforge.model;

public class Skill {
    private int id;
    private int userId;
    private String skillName;
    private boolean isAcquired; // True = Blue Node (Have), False = Red Node (Missing)

    // Constructor for New Skill
    public Skill(int userId, String skillName, boolean isAcquired) {
        this.userId = userId;
        this.skillName = skillName;
        this.isAcquired = isAcquired;
    }

    // Constructor from DB
    public Skill(int id, int userId, String skillName, boolean isAcquired) {
        this.id = id;
        this.userId = userId;
        this.skillName = skillName;
        this.isAcquired = isAcquired;
    }

    // GETTERS & SETTERS
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return this.userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getSkillName() { return this.skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public boolean isAcquired() { return this.isAcquired; }
    public void setAcquired(boolean acquired) { this.isAcquired = acquired; }

    @Override
    public String toString() {
        return (this.isAcquired ? "[✔️] " : "[❌] ") + this.skillName;
    }
}
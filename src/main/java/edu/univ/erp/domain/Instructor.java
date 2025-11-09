package edu.univ.erp.domain;

/**
 * Represents an instructor profile in the ERP system.
 */
public class Instructor {
    private int instructorId; // Matches the user_id in the Auth DB
    private String department;

    // Constructor
    public Instructor() {}

    // Getters and Setters
    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}


package edu.univ.erp.domain;

/**
 * Represents a course in the ERP system.
 */
public class Course {
    private int courseId;
    private String code;
    private String title;
    private double credits;

    // Constructor
    public Course() {}

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getCredits() { return credits; }
    public void setCredits(double credits) { this.credits = credits; }
}


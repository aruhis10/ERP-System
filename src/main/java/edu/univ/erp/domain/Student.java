package edu.univ.erp.domain;

// Represents the specific profile data for a student from the ERP DB.
// This data includes their academic enrollment details.
public class Student {
    private int studentId; // Matches the user_id in the Auth DB
    private String rollNo;
    private String program; // e.g., B.Tech CS, M.Sc. Math
    private int year;      // Academic year of enrollment

    // Constructor
    public Student() {}

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}

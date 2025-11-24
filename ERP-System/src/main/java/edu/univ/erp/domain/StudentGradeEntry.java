package edu.univ.erp.domain;

/**
 * DTO representing a student row in a gradebook roster.
 */
public class StudentGradeEntry {

    private final int enrollmentId;
    private final int studentId;
    private final String rollNo;
    private final String studentName;
    private final Double finalGrade;

    public StudentGradeEntry(int enrollmentId, int studentId, String rollNo, String studentName, Double finalGrade) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.finalGrade = finalGrade;
    }

    public Double getFinalGrade() { return finalGrade; }
    public int getEnrollmentId() { return enrollmentId; }
    public int getStudentUserId() { return studentId; }
    public String getRollNo() { return rollNo; }
    public String getStudentName() { return studentName; }
}

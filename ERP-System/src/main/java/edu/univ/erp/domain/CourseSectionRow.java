package edu.univ.erp.domain;

public class CourseSectionRow {

    private final String code;
    private final String title;
    private final double credits;
    private final String scheduleAndRoom;
    private final int capacity;
    private final int enrolled;
    private final String instructorName;
    private final int sectionId;
    private final String sectionName;

    public CourseSectionRow(String code,
                            String title,
                            double credits,
                            String scheduleAndRoom,
                            int capacity,
                            int enrolled,
                            String instructorName,
                            int sectionId,
                            String sectionName) {

        this.code = code;
        this.title = title;
        this.credits = credits;
        this.scheduleAndRoom = scheduleAndRoom;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.instructorName = instructorName;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
    }

    // ---------------------------------------
    // REQUIRED GETTERS (You were missing these!)
    // ---------------------------------------

    public String getCode() {
        return code;
    }
    public String getTitle() {
        return title;
    }
    public double getCredits() {
        return credits;
    }
    public String getScheduleAndRoom() {
        return scheduleAndRoom;
    }

    public int getCapacity() {
        return capacity;
    }
    public int getEnrolled() {
        return enrolled;
    }
    public String getInstructorName() {
        return instructorName;
    }
    public int getSectionId() {
        return sectionId;
    }
    public String getSectionName() {
        return sectionName;
    }
    // Full array (used by catalog table)
    public Object[] toRowArrayWithId() {
        return new Object[]{
                this.code,
                this.title,
                this.credits,
                this.scheduleAndRoom,
                this.capacity,
                this.instructorName,
                this.sectionName
        };
    }
}

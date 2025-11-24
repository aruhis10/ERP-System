package edu.univ.erp.domain;

public class InstructorSectionRow {

    // --- FIELDS ---
    private  int sectionId;
    private  String sectionName;
    private  String courseCode;
    private  String courseTitle;
    private  double credits;
    private  String scheduleAndRoom;
    private  int capacity;
    private  int enrolledCount;


    public InstructorSectionRow(int sectionId, String sectionName, String courseCode, String courseTitle,
                                double credits, String scheduleAndRoom, int capacity,
                                int enrolledCount) {

        // Initialize ALL final fields
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        this.scheduleAndRoom = scheduleAndRoom;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
    }
    public int getSectionId() { return sectionId; }
    public String getSectionName() { return sectionName; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public double getCredits() { return credits; }
    public String getScheduleAndRoom() { return scheduleAndRoom; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }
    public Object[] toRowArray() {
        return new Object[]{
                courseCode,
                courseTitle,
                credits,
                scheduleAndRoom,
                enrolledCount + " / " + capacity,
                sectionName
        };
    }
}

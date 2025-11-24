package edu.univ.erp.service;

import edu.univ.erp.data.DAOs.CourseDAO;
import edu.univ.erp.data.DAOs.SectionDAO;
import edu.univ.erp.domain.ServiceResult;

import java.util.List;

public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();

    // ----------------------------------------
    // ADD COURSE
    // ----------------------------------------
    public ServiceResult addCourse(String code, String title, int credits) {
        try {
            courseDAO.insertCourse(code, title, credits);
            return ServiceResult.success("Course added successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Error adding course: " + e.getMessage());
        }
    }

    // ----------------------------------------
    // DELETE COURSE + its sections
    // ----------------------------------------
    public ServiceResult deleteCourse(int courseId) {
        try {
            sectionDAO.deleteSectionsByCourse(courseId);     // unassign instructors + delete sections
            courseDAO.deleteCourse(courseId);               // delete course
            return ServiceResult.success("Course deleted with all sections.");
        } catch (Exception e) {
            return ServiceResult.failure("Error deleting course: " + e.getMessage());
        }
    }

    // ----------------------------------------
    // ADD SECTION
    // ----------------------------------------
    public ServiceResult addSection(int courseId, String sectionName, String dayTime,
                                    String room, int capacity, String semester, int year) {
        try {
            sectionDAO.insertSection(courseId, sectionName, dayTime, room, capacity, semester, year);
            return ServiceResult.success("Section added successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Error adding section: " + e.getMessage());
        }
    }

    // ----------------------------------------
    // ASSIGN INSTRUCTOR
    // ----------------------------------------
    public ServiceResult assignInstructor(int sectionId, int instructorId) {
        try {
            sectionDAO.assignInstructor(sectionId, instructorId);
            return ServiceResult.success("Instructor assigned.");
        } catch (Exception e) {
            return ServiceResult.failure("Error assigning instructor: " + e.getMessage());
        }
    }

    // ----------------------------------------
    // UNASSIGN INSTRUCTOR
    // ----------------------------------------
    public ServiceResult unassignInstructor(int sectionId) {
        try {
            sectionDAO.unassignInstructor(sectionId);
            return ServiceResult.success("Instructor unassigned.");
        } catch (Exception e) {
            return ServiceResult.failure("Error unassigning instructor: " + e.getMessage());
        }
    }
    public List<String[]> getFullCourseSectionData() {
        return courseDAO.getFullCourseSectionRows();
    }
    public ServiceResult deleteSection(int courseId, String sectionName) {
        try {
            Integer sectionId = courseDAO.getSectionId(courseId, sectionName);

            if (sectionId == null) {
                return ServiceResult.failure("Section not found for this course.");
            }

            // delete in correct order
            courseDAO.deleteGradesForSection(sectionId);
            courseDAO.deleteEnrollmentsForSection(sectionId);
            courseDAO.deleteSection(sectionId);

            return ServiceResult.success("Section '" + sectionName + "' deleted.");
        } catch (Exception e) {
            return ServiceResult.failure("Error deleting section: " + e.getMessage());
        }
    }
    public Integer getSectionId(int courseId, String sectionName) {
        try {
            return courseDAO.getSectionId(courseId, sectionName);
        } catch (Exception e) {
            return null;
        }
    }

}

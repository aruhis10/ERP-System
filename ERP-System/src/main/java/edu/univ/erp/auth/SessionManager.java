package edu.univ.erp.auth;

import edu.univ.erp.domain.AuthUser;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;

/**
 * Manages the current user session in the application.
 * This is a singleton that holds the authenticated user and their profile data.
 */
public class SessionManager {
    private static SessionManager instance;
    
    private AuthUser currentUser;
    private Student studentProfile;  // Only set if role is "Student"
    private Instructor instructorProfile;  // Only set if role is "Instructor"
    
    private SessionManager() {
        // Private constructor for singleton pattern
    }
    
    /**
     * Gets the singleton instance of SessionManager.
     * @return The SessionManager instance.
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Establishes a new session with the authenticated user.
     * @param user The authenticated AuthUser.
     * @param studentProfile The student profile if user is a student, null otherwise.
     * @param instructorProfile The instructor profile if user is an instructor, null otherwise.
     */
    public void establishSession(AuthUser user, Student studentProfile, Instructor instructorProfile) {
        this.currentUser = user;
        this.studentProfile = studentProfile;
        this.instructorProfile = instructorProfile;
    }
    
    /**
     * Clears the current session (logout).
     */
    public void clearSession() {
        this.currentUser = null;
        this.studentProfile = null;
        this.instructorProfile = null;
    }
    
    /**
     * Checks if a user is currently logged in.
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Gets the current authenticated user.
     * @return The AuthUser if logged in, null otherwise.
     */
    public AuthUser getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the student profile for the current user.
     * @return The Student profile if user is a student, null otherwise.
     */
    public Student getStudentProfile() {
        return studentProfile;
    }
    
    /**
     * Gets the instructor profile for the current user.
     * @return The Instructor profile if user is an instructor, null otherwise.
     */
    public Instructor getInstructorProfile() {
        return instructorProfile;
    }
}


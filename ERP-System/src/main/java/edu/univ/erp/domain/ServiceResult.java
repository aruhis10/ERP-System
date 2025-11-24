package edu.univ.erp.domain;

/**
 * Container to pass the result of a Service Layer operation (e.g., success/failure message) back to the UI.
 */
public class ServiceResult {
    private final boolean success;
    private final String message;

    public ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }
    public static ServiceResult success(String msg) {
        return new ServiceResult(true, msg);
    }

    public static ServiceResult failure(String msg) {
        return new ServiceResult(false, msg);
    }

    public String getMessage() {
        return message;
    }
}
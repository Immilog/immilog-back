package com.backend.immilog.report.exception;

public enum ReportErrorCode {

    INVALID_REPORT_TARGET("R001", "Invalid report target"),
    INVALID_REPORTER("R002", "Invalid reporter"),
    INVALID_REPORT_REASON("R003", "Invalid report reason"),
    INVALID_REPORT_DESCRIPTION("R004", "Invalid report description"),
    CANNOT_REPORT_YOURSELF("R005", "Cannot report yourself"),
    DUPLICATE_REPORT("R006", "Report already exists"),
    ALREADY_REPORTED("R007", "Already reported"),

    REPORT_NOT_FOUND("R010", "Report not found"),
    INVALID_STATUS_TRANSITION("R011", "Invalid status transition"),
    CANNOT_UPDATE_PROCESSED_REPORT("R012", "Cannot update processed report"),

    UNAUTHORIZED_REPORT_ACCESS("R020", "Unauthorized report access"),
    REPORT_ACCESS_DENIED("R021", "Report access denied"),

    REPORT_SAVE_ERROR("R090", "Report save error"),
    REPORT_DELETE_ERROR("R091", "Report delete error"),
    INTERNAL_SERVER_ERROR("R099", "Internal server error");

    private final String code;
    private final String message;

    ReportErrorCode(
            String code,
            String message
    ) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
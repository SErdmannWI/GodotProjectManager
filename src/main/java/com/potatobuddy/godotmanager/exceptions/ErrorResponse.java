package com.potatobuddy.godotmanager.exceptions;

public class ErrorResponse {
    private int errorCode;
    private String errorMessage;

    public ErrorResponse() {}

    public ErrorResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

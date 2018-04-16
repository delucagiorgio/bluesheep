package com.betfair.foe.exceptions;

public class FOEException extends Throwable {

    private String errorDetails;
    private String errorCode;
    private String errorCause;
    private String requestUUID;

    public FOEException() {
        super();
    }

    public FOEException(String errorDetails, String errorCode, String errorCause, String requestUUID) {
        this.errorCode=errorCode;
        this.errorDetails=errorDetails;
        this.errorCause=errorCause;
        this.requestUUID=requestUUID;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
    public String getErrorCause() {
        return errorCause;
    }
    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getRequestUUID() {
        return requestUUID;
    }
    public void setRequestUUID(String requestUUID) {
        this.requestUUID = requestUUID;
    }

    @Override
    public String toString() {
        return "ErrorCode: " + errorCode + " ErrorCause: " + errorCause
                + " ErrorDetails: " + errorDetails + " RequestUUID: " + requestUUID;
    }

}

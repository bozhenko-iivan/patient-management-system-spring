package com.pm.patientservice.exception;

public class PatientNotExistException extends RuntimeException {
    public PatientNotExistException(String message) {
        super(message);
    }
}

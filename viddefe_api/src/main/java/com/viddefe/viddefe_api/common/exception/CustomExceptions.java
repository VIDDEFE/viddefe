package com.viddefe.viddefe_api.common.exception;

public class CustomExceptions {

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }


    public static class ResourceAlreadyExistsException extends RuntimeException {
        public ResourceAlreadyExistsException(String message) {
            super(message);
        }
    }


    public static class BussinesException extends RuntimeException {
        public BussinesException(String message) {
            super(message);
        }
    }
}
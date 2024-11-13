package com.education.flashEng.exception;

public class EntityNotFoundWithIdException extends RuntimeException {
    public EntityNotFoundWithIdException(String entityName, String id) {
        super(entityName + " not found with id: " + id);
    }
}
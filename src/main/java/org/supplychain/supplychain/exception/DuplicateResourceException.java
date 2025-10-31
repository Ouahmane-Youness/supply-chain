package org.supplychain.supplychain.exception;

// Exception quand une ressource est déjà utilisée ou existe déjà
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
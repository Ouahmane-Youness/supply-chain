package org.supplychain.supplychain.exception;


// Exception quand une ressource n'existe pas
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

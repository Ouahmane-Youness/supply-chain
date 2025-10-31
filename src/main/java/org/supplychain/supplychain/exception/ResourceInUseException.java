package org.supplychain.supplychain.exception;

// Exception quand une ressource est en conflit ou ne peut pas être supprimée
public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}
package main.exception;

// Custom exception!!!!
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}

package cz.cvut.ear.DarkstoreApi.exception;

public class CourierNotFoundException extends RuntimeException {
    public CourierNotFoundException(String message) {
        super(message);
    }
}

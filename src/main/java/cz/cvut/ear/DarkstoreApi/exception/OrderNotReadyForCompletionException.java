package cz.cvut.ear.DarkstoreApi.exception;

public class OrderNotReadyForCompletionException extends IllegalArgumentException {
    public OrderNotReadyForCompletionException(String message) {
        super(message);
    }
}

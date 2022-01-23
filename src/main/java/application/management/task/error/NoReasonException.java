package application.management.task.error;

public class NoReasonException extends RuntimeException {

    public NoReasonException(String message) {
        super(message);
    }
}

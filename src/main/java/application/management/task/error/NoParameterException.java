package application.management.task.error;

public class NoParameterException extends RuntimeException {
    public NoParameterException(String message) {
        super(message);
    }
}

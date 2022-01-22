package application.management.task.error;

public class WrongStateException extends RuntimeException {

    public WrongStateException(String message) {
        super(message);
    }
}

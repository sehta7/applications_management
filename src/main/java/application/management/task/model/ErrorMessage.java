package application.management.task.model;

public enum ErrorMessage {
    ENTITY_NOT_EXIST("Object with id=[%s] does not exist."),
    WRONG_STATE("Object needs to be in state: %s.");

    public String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}

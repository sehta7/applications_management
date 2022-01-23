package application.management.task.model;

public enum ErrorMessage {
    ENTITY_NOT_EXIST("Object with id=[%s] does not exist."),
    WRONG_STATE("Object needs to be in state: %s."),
    NO_PARAMETER("To add application provide name and content."),
    NO_REASON("To delete or reject application provide the reason.");

    public String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}

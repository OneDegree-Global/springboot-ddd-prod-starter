package hk.onedegree.application.exception;

public class CreateUserFailsException extends Exception {
    public CreateUserFailsException(String errorMessage) {
        super(errorMessage);
    }
}

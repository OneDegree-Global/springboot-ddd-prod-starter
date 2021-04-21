package hk.onedegree.application.exception;

public class UnAuthorizeException extends Exception {
    public UnAuthorizeException(String errorMessage) {
        super(errorMessage);
    }
}

package hk.onedegree.application.aspect.exception;

public class UnAuthorizeException extends Exception {
    public UnAuthorizeException(String errorMessage) {
        super(errorMessage);
    }
}

package hk.onedegree.domain.auth.exceptions;

public class InValidPasswordException extends Exception {
    public InValidPasswordException(String errorMessage) {
        super(errorMessage);
    }
}

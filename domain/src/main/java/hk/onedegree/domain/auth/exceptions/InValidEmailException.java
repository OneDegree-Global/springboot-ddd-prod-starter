package hk.onedegree.domain.auth.exceptions;

public class InValidEmailException extends Exception {
    public InValidEmailException(String errorMessage) {
        super(errorMessage);
    }
}

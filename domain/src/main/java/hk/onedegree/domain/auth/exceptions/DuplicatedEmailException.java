package hk.onedegree.domain.auth.exceptions;

public class DuplicatedEmailException extends Exception {
    public DuplicatedEmailException(String errorMessage) {
        super(errorMessage);
    }
}

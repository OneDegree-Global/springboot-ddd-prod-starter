package hk.onedegree.domain.auth.exceptions;

public class UnsupportedAlgException extends Exception {
    public UnsupportedAlgException(String errorMessage) {
        super(errorMessage);
    }
}

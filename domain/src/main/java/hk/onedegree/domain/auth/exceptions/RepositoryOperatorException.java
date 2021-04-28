package hk.onedegree.domain.auth.exceptions;

public class RepositoryOperatorException extends Exception {
    public RepositoryOperatorException(String errorMessage) {
        super(errorMessage);
    }

    public RepositoryOperatorException(Throwable cause) {
        super(cause);
    }
}

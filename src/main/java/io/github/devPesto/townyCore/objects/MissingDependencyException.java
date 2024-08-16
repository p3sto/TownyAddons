package io.github.devPesto.townyCore.objects;

public class MissingDependencyException extends RuntimeException {

    public MissingDependencyException() {
        super();
    }

    public MissingDependencyException(String message) {
        super(message);
    }

    public MissingDependencyException(Throwable cause) {
        super(cause);
    }
}

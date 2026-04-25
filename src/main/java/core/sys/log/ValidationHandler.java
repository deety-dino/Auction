package core.sys.log;

public interface ValidationHandler<T> {
    void setNext(ValidationHandler<T> next);

    AuthResult handle(T request);
}


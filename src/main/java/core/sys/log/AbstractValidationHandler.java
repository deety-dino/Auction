package core.sys.log;

public abstract class AbstractValidationHandler<T> implements ValidationHandler<T> {
    private ValidationHandler<T> next;

    @Override
    public void setNext(ValidationHandler<T> next) {
        this.next = next;
    }

    @Override
    public AuthResult handle(T request) {
        AuthResult currentResult = validate(request);
        if (!currentResult.isSuccess()) {
            return currentResult;
        }

        if (next == null) {
            return AuthResult.success("Validation passed");
        }
        return next.handle(request);
    }

    protected abstract AuthResult validate(T request);
}


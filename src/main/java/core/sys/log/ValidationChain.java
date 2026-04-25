package core.sys.log;

public final class ValidationChain {
    private ValidationChain() {
    }


    @SafeVarargs
    public static <T> ValidationHandler<T> link(ValidationHandler<T> first, ValidationHandler<T>... chain) {
        ValidationHandler<T> current = first;
        for (ValidationHandler<T> next : chain) {
            current.setNext(next);
            current = next;
        }
        return first;
    }
}


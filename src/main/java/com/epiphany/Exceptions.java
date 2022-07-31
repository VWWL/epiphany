package com.epiphany;

public interface Exceptions {
    static RunnableExceptions execute(RunnableWithCheckedException runnable) {
        return new RunnableExceptions(runnable);
    }

    static <R> EvaluateExceptions<R> evaluate(SupplierWithCheckedException<R> supplier) {
        return new EvaluateExceptions<>(supplier);
    }

    static void ignored(final RunnableWithCheckedException runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {
        }
    }

    static <R> R ignored(final SupplierWithCheckedException<R> supplier, final R defaultR) {
        try {
            return supplier.get();
        } catch (Exception ignored) {
            return defaultR;
        }
    }

    static RuntimeException wrap(final Throwable throwable) {
        if (throwable instanceof RuntimeException) return (RuntimeException) throwable;
        return new RuntimeException(throwable);
    }
}

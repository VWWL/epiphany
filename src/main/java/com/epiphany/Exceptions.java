package com.epiphany;

public interface Exceptions {
    static RunnableExceptions execute(RunnableWithCheckedException runnable) {
        return new RunnableExceptions(runnable);
    }

    static <R> EvaluateExceptions<R> evaluate(SupplierWithCheckedException<R> supplier) {
        return new EvaluateExceptions<>(supplier);
    }

    static void ignored(RunnableWithCheckedException runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {
        }
    }

    static <R> R ignored(SupplierWithCheckedException<R> supplier, R defaultR) {
        try {
            return supplier.get();
        } catch (Exception ignored) {
            return defaultR;
        }
    }

    static RuntimeException wrap(Throwable throwable) {
        if (throwable instanceof RuntimeException) return (RuntimeException) throwable;
        return new RuntimeException(throwable);
    }
}

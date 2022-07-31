package com.epiphany;

public interface Exceptions {
    static void execute(RunnableWithCheckedException runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw wrap(e);
        }
    }

    static <R> R evaluate(SupplierWithCheckedException<R> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw wrap(e);
        }
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

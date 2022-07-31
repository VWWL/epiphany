package com.epiphany;

public class EvaluateExceptions<R> implements Exceptions {
    private final SupplierWithCheckedException<R> supplier;

    public EvaluateExceptions(final SupplierWithCheckedException<R> supplier) {
        this.supplier = supplier;
    }

    public R evaluate() {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw Exceptions.wrap(e);
        }
    }

    public R elseThrow(final String message) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(message);
        }
    }
}

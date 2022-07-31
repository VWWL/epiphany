package com.epiphany;

public class EvaluateExceptions<R> implements Exceptions {
    private final SupplierWithCheckedException<R> supplier;

    public EvaluateExceptions(SupplierWithCheckedException<R> supplier) {
        this.supplier = supplier;
    }

    public R evaluate() {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw Exceptions.wrap(e);
        }
    }
}

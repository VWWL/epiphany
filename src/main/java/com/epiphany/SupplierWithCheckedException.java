package com.epiphany;

@FunctionalInterface
public interface SupplierWithCheckedException<T> {
    T get() throws Exception;
}

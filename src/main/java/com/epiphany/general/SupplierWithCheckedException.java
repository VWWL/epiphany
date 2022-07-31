package com.epiphany.general;

@FunctionalInterface
public interface SupplierWithCheckedException<T> {
    T get() throws Exception;
}

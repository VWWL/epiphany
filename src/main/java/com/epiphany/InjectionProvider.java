package com.epiphany;

@FunctionalInterface
public interface InjectionProvider<T> {

    T get();

}

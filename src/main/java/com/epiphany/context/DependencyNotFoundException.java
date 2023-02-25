package com.epiphany.context;

public final class DependencyNotFoundException extends RuntimeException {
    private final Class<?> dependency;
    private final Class<?> component;

    public DependencyNotFoundException(final Class<?> dependency, final Class<?> component) {
        this.dependency = dependency;
        this.component = component;
    }

    public Class<?> dependency() {
        return dependency;
    }

    public Class<?> component() {
        return component;
    }
}

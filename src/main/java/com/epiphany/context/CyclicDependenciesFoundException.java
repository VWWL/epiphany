package com.epiphany.context;

import java.util.LinkedHashSet;
import java.util.Set;

public class CyclicDependenciesFoundException extends RuntimeException {
    private final Set<Class<?>> components;

    public CyclicDependenciesFoundException(Class<?> component) {
        this.components = new LinkedHashSet<>();
        this.components.add(component);
    }

    public <Type> CyclicDependenciesFoundException(Class<Type> component, CyclicDependenciesFoundException e) {
        this(component);
        this.components.addAll(e.components);
    }

    public Set<Class<?>> components() {
        return components;
    }
}

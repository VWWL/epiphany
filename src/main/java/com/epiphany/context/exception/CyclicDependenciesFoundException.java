package com.epiphany.context.exception;

import java.util.*;

public final class CyclicDependenciesFoundException extends RuntimeException {

    private final Set<Class<?>> components;

    public CyclicDependenciesFoundException(final Stack<Class<?>> visiting) {
        components = new LinkedHashSet<>();
        components.addAll(visiting);
    }

    public Set<Class<?>> components() {
        return components;
    }

}

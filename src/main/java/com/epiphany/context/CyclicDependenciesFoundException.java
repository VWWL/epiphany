package com.epiphany.context;

import java.util.*;

public class CyclicDependenciesFoundException extends RuntimeException {

    private final Set<Class<?>> components = new LinkedHashSet<>();

    public CyclicDependenciesFoundException(Stack<Class<?>> visiting) {
        components.addAll(visiting);
    }

    public Set<Class<?>> components() {
        return components;
    }

}

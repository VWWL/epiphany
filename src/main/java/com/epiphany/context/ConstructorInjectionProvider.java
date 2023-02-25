package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

class ConstructorInjectionProvider<Type> implements Provider<Type> {

    private final Constructor<Type> injectConstructor;
    private final List<Class<?>> dependencies;

    @SuppressWarnings("unchecked")
    public ConstructorInjectionProvider(final Class<Type> implementation) {
        this.injectConstructor = (Constructor<Type>) stream(implementation.getConstructors())
            .filter(c -> c.isAnnotationPresent(Inject.class))
            .findFirst()
            .orElseGet(() -> evaluate(implementation::getConstructor).evaluate());
        this.dependencies = stream(injectConstructor.getParameters()).map(Parameter::getType).collect(Collectors.toList());
    }

    @Override
    public Type get(final Context context) {
        Object[] dependencies = stream(injectConstructor.getParameters())
            .map(Parameter::getType)
            .map(context::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Object[]::new);
        return evaluate(() -> injectConstructor.newInstance(dependencies)).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return dependencies;
    }

}

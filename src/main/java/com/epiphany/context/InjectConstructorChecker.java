package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;

import static java.util.Arrays.stream;

public class InjectConstructorChecker<Type> {

    private final Class<Type> component;

    public InjectConstructorChecker(Class<Type> component) {
        this.component = component;
    }

    public Class<Type> component() {
        return component;
    }

    public void check() {
        Class<Type> component = component();
        if (Modifier.isAbstract(component.getModifiers())) throw new IllegalComponentException();
        if (InjectStream.of(component.getConstructors()).injectablePart().count() > 1) throw new IllegalComponentException();
        if (noInjectConstructor() && noDefaultConstructor()) throw new IllegalComponentException();
    }

    private boolean noInjectConstructor() {
        return InjectStream.of(component.getConstructors()).injectablePart().findAny().isEmpty();
    }

    private boolean noDefaultConstructor() {
        return stream(component.getConstructors()).filter(this::noParams).findFirst().isEmpty();
    }

    private boolean noParams(final Constructor<?> constructor) {
        return constructor.getParameters().length == 0;
    }

}

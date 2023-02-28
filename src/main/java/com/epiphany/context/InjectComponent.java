package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;

import static java.util.Arrays.stream;

class InjectComponent<Type> {

    private final Class<Type> component;

    public InjectComponent(final Class<Type> component) {
        this.component = component;
    }

    public void check() {
        if (Modifier.isAbstract(component.getModifiers())) throw new IllegalComponentException();
        if (oneMoreInjectConstructors()) throw new IllegalComponentException();
        if (noInjectConstructor() && noDefaultConstructor()) throw new IllegalComponentException();
    }

    private boolean oneMoreInjectConstructors() {
        return InjectStream.of(component.getConstructors()).injectablePart().count() > 1;
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

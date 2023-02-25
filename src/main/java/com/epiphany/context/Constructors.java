package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class Constructors<Type> {

    private final Constructor<Type> injectConstructor;

    public Constructors(Class<Type> component) {
        this.injectConstructor = initInjectConstructor(component);
    }

    public Constructor<Type> get() {
        return injectConstructor;
    }

    @SuppressWarnings("unchecked")
    static <Type> Constructor<Type> initInjectConstructor(Class<Type> component) {
        return (Constructor<Type>) injectableStream(component.getConstructors()).findFirst().orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

    private static <T extends AnnotatedElement> Stream<T> injectableStream(T[] declaredFields) {
        return stream(declaredFields).filter(o -> o.isAnnotationPresent(Inject.class));
    }

}

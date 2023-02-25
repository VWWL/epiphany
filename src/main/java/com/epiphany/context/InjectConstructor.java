package com.epiphany.context;

import java.lang.reflect.*;
import java.util.Optional;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class InjectConstructor<Type> {

    private final Constructor<Type> impl;

    @SuppressWarnings("unchecked")
    public InjectConstructor(Class<Type> component) {
        new InjectConstructorChecker<>(component).check();
        this.impl = (Constructor<Type>) InjectStream.of(component.getConstructors()).injectablePart().findFirst().orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

    public Stream<Class<?>> dependencies() {
        return stream(impl.getParameterTypes());
    }

    @SuppressWarnings("all")
    public Type newInstance(Context context, InjectFields injectFields, InjectMethods injectMethods) {
        Object[] dependencies = stream(impl.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
        Type instance = evaluate(() -> impl.newInstance(dependencies)).evaluate();
        injectFields.injectInto(context, instance);
        injectMethods.injectInto(context, instance);
        return instance;
    }

}

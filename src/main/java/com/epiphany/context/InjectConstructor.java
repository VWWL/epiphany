package com.epiphany.context;

import java.lang.reflect.*;
import java.util.Optional;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

class InjectConstructor<Type> {

    private final Constructor<Type> impl;

    @SuppressWarnings("unchecked")
    public InjectConstructor(final Class<Type> component) {
        new InjectComponent<>(component).check();
        this.impl = (Constructor<Type>) InjectStream.of(component.getConstructors()).injectablePart().findFirst().orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

    public Stream<Class<?>> dependencies() {
        return stream(impl.getParameterTypes());
    }

    @SuppressWarnings("all")
    public Type newInstance(final Context context, final InjectFields injectFields, final InjectMethods injectMethods) {
        Object[] dependencies = stream(impl.getParameters()).map(p -> {
            java.lang.reflect.Type type = p.getParameterizedType();
            if (type instanceof ParameterizedType) return context.get((ParameterizedType) type);
            return context.get((Class<?>) type);
        }).map(Optional::get).toArray(Object[]::new);
        Type instance = evaluate(() -> impl.newInstance(dependencies)).evaluate();
        injectFields.injectInto(context, instance);
        injectMethods.injectInto(context, instance);
        return instance;
    }

}

package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.Optional;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class InjectConstructor<Type> {

    private final Constructor<Type> impl;

    @SuppressWarnings("unchecked")
    public InjectConstructor(Class<Type> component) {
        checkConstructor(component);
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

    <Type> void checkConstructor(final Class<Type> component) {
        if (Modifier.isAbstract(component.getModifiers())) throw new IllegalComponentException();
        if (countOfInjectConstructors(component) > 1) throw new IllegalComponentException();
        if (countOfInjectConstructors(component) == 0 && noDefaultConstructor(component)) throw new IllegalComponentException();
    }

    private static <Type> boolean noDefaultConstructor(final Class<Type> implementation) {
        return stream(implementation.getConstructors()).filter(InjectConstructor::noParams).findFirst().isEmpty();
    }

    private static boolean noParams(final Constructor<?> constructor) {
        return constructor.getParameters().length == 0;
    }

    private static <Type> long countOfInjectConstructors(final Class<Type> implementation) {
        return stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).count();
    }

}

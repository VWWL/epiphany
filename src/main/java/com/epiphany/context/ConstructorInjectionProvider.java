package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public final class ConstructorInjectionProvider<Type> implements Provider<Type> {

    private final Constructor<Type> injectConstructor;
    private final List<Field> injectFields;

    public ConstructorInjectionProvider(final Class<Type> component) {
        this.injectConstructor = initInjectConstructor(component);
        this.injectFields = initInjectFields(component);
    }

    @Override
    @SuppressWarnings("all")
    public Type get(final Context context) {
        Object[] dependencies = stream(injectConstructor.getParameters())
            .map(Parameter::getType)
            .map(context::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Object[]::new);
        return evaluate(() -> {
            Type instance = injectConstructor.newInstance(dependencies);
            for (Field field : injectFields) {
                field.setAccessible(true);
                field.set(instance, context.get(field.getType()).get());
            }
            return instance;
        }).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return Stream.concat(
            injectFields.stream().map(Field::getType),
            stream(injectConstructor.getParameters()).map(Parameter::getType)
        ).collect(Collectors.toList());
    }

    private static <Type> List<Field> initInjectFields(Class<Type> component) {
        List<Field> injectFields = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            injectFields.addAll(stream(current.getDeclaredFields()).filter(o -> o.isAnnotationPresent(Inject.class)).toList());
            current = current.getSuperclass();
        }
        return injectFields;
    }

    @SuppressWarnings("unchecked")
    private static <Type> Constructor<Type> initInjectConstructor(Class<Type> component) {
        return (Constructor<Type>) stream(component.getConstructors())
            .filter(c -> c.isAnnotationPresent(Inject.class))
            .findFirst()
            .orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

}

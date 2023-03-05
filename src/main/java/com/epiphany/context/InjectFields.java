package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.execute;

class InjectFields {

    private final List<Field> impl;

    public <Type> InjectFields(final Class<Type> component) {
        this.impl = new Traverser<Field>().traverse(component, (methods, current) -> InjectStream.of(current.getDeclaredFields()).injectablePart().toList());
        if (impl.stream().anyMatch(o -> Modifier.isFinal(o.getModifiers()))) throw new IllegalComponentException();
    }

    @SuppressWarnings("all")
    public <Type> void injectInto(final Context context, final Type instance) {
        for (Field field : impl) {
            field.setAccessible(true);
            execute(() -> field.set(instance, getByType(context, field).get())).run();
        }
    }

    @SuppressWarnings("all")
    private Optional<?> getByType(Context context, Field field) {
        java.lang.reflect.Type type = field.getGenericType();
        if (type instanceof ParameterizedType) return context.get((ParameterizedType) type);
        return context.get((Class) type);
    }

    public Stream<? extends Class<?>> dependencies() {
        return impl.stream().map(Field::getType);
    }

}

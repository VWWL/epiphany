package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.List;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.execute;
import static java.util.Arrays.stream;

public class InjectFields {

    private final List<Field> impl;

    public <Type> InjectFields(Class<Type> component) {
        this.impl = new Traverser<Field>().traverse(component, (methods, current) -> InjectStream.of(current.getDeclaredFields()).injectablePart().toList());
        if (impl.stream().anyMatch(o -> Modifier.isFinal(o.getModifiers()))) throw new IllegalComponentException();
    }

    @SuppressWarnings("all")
    public <Type> void injectInto(Context context, Type instance) {
        for (Field field : impl) {
            field.setAccessible(true);
            execute(() -> field.set(instance, context.get(field.getType()).get())).run();
        }
    }

    public Stream<? extends Class<?>> dependencies() {
        return impl.stream().map(Field::getType);
    }

}

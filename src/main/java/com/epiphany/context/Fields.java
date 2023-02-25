package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.List;

import static com.epiphany.general.Exceptions.*;
import static java.util.Arrays.stream;

public class Fields {

    private final List<Field> impl;

    public <Type> Fields(Class<Type> component) {
        this.impl = new Traverser<Field>().traverse(component, (injectMethods1, current) -> stream(current.getDeclaredFields()).filter(o -> o.isAnnotationPresent(Inject.class)).toList());
        if (impl.stream().anyMatch(o -> Modifier.isFinal(o.getModifiers()))) throw new IllegalComponentException();
    }

    public List<Field> get() {
        return impl;
    }

    public <Type> void injectInto(Context context, Type instance) {
        for (Field field : impl) {
            field.setAccessible(true);
            execute(() -> field.set(instance, toDependency(context, field))).run();
        }
    }

    @SuppressWarnings("all")
    private static Object toDependency(Context context, Field field) {
        return context.get(field.getType()).get();
    }

}

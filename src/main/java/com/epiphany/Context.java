package com.epiphany;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.epiphany.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class Context {
    private final Map<Class<?>, Provider<?>> providers;

    public Context() {
        this.providers = new HashMap<>();
    }

    public <Type> void bind(final Class<Type> type, final Type instance) {
        providers.put(type, () -> instance);
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(final Class<Type> type) {
        return (Type) providers.get(type).get();
    }

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementation) {
        providers.put(type, () -> {
            Constructor<Implementation> injectConstructor = injectConstructor(implementation);
            Object[] dependencies = stream(injectConstructor.getParameters()).map(p -> this.get(p.getType())).toArray(Object[]::new);
            return evaluate(() -> injectConstructor.newInstance(dependencies));
        });
    }

    @SuppressWarnings("unchecked")
    private <Type> Constructor<Type> injectConstructor(final Class<Type> implementation) {
        Stream<Constructor<?>> injectConstructors = stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class));
        return (Constructor<Type>) injectConstructors.findFirst().orElseGet(() -> evaluate(implementation::getConstructor).evaluate());
    }
}

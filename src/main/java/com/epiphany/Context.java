package com.epiphany;

import jakarta.inject.Provider;

import java.util.HashMap;
import java.util.Map;

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

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementationType) {
        providers.put(type, () -> Exceptions.evaluate(() -> implementationType.getConstructor().newInstance()));
    }
}

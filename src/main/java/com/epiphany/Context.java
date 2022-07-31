package com.epiphany;

import jakarta.inject.Provider;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Map<Class<?>, Provider<?>> providers;

    public Context() {
        this.providers = new HashMap<>();
    }

    public <ComponentType> void bind(final Class<ComponentType> type, final ComponentType instance) {
        providers.put(type, () -> instance);
    }

    @SuppressWarnings("unchecked")
    public <ComponentType> ComponentType get(final Class<ComponentType> type) {
        return (ComponentType) providers.get(type).get();
    }

    public <ComponentType, ComponentImplementation extends ComponentType>
    void bind(final Class<ComponentType> type, final Class<ComponentImplementation> implementationType) {
        providers.put(type, () -> Exceptions.evaluate(() -> implementationType.getConstructor().newInstance()));
    }
}

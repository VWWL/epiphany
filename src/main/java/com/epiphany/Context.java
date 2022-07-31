package com.epiphany;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Map<Class<?>, Object> components;

    public Context() {
        this.components = new HashMap<>();
    }

    public <ComponentType> void bind(final Class<ComponentType> type, final ComponentType instance) {
        components.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public <ComponentType> ComponentType get(final Class<ComponentType> type) {
        return (ComponentType) components.get(type);
    }

    public <ComponentType, ComponentImplementation extends ComponentType>
    void bind(final Class<ComponentType> type, final Class<ComponentImplementation> implementationType) {

    }
}

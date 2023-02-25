package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.util.*;

import static java.util.Arrays.stream;

public final class ContextConfig {

    private final Map<Class<?>, Provider<?>> providers;

    public ContextConfig() {
        this.providers = new HashMap<>();
    }

    public <Type> void bind(final Class<Type> type, final Type instance) {
        providers.put(type, context -> instance);
    }

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementation) {
        checkConstructor(implementation);
        providers.put(type, new ConstructorInjectionProvider<>(implementation));
    }

    public Context context() {
        providers.keySet().forEach(component -> checkDependencies(component, new Stack<>()));
        return new Context() {
            @Override
            @SuppressWarnings("unchecked")
            public <Type> Optional<Type> get(Class<Type> type) {
                return Optional.ofNullable(providers.get(type)).map(provider -> provider.get(this)).map(o -> (Type) o);
            }
        };
    }

    private <Type> void checkConstructor(final Class<Type> implementation) {
        if (noConstructor(implementation)) return;
        if (countOfInjectConstructors(implementation) > 1) throw new IllegalComponentException();
        if (countOfInjectConstructors(implementation) == 0 && noDefaultConstructor(implementation)) throw new IllegalComponentException();
    }

    private <Type> boolean noConstructor(Class<Type> implementation) {
        return stream(implementation.getConstructors()).findAny().isEmpty();
    }

    private <Type> boolean noDefaultConstructor(final Class<Type> implementation) {
        return stream(implementation.getConstructors()).filter(this::noParams).findFirst().isEmpty();
    }

    private boolean noParams(final Constructor<?> constructor) {
        return constructor.getParameters().length == 0;
    }

    private <Type> long countOfInjectConstructors(final Class<Type> implementation) {
        return stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).count();
    }

    private void checkDependencies(final Class<?> component, final Stack<Class<?>> visiting) {
        for (Class<?> dependency : providers.get(component).dependencies()) {
            if (!providers.containsKey(dependency)) throw new DependencyNotFoundException(dependency, component);
            if (visiting.contains(dependency)) throw new CyclicDependenciesFoundException(visiting);
            visiting.push(dependency);
            checkDependencies(dependency, visiting);
            visiting.pop();
        }
    }

}

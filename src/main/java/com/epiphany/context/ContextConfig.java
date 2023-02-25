package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class ContextConfig {

    private final Map<Class<?>, Provider<?>> providers;
    private final Map<Class<?>, List<Class<?>>> dependencies;

    public ContextConfig() {
        this.providers = new HashMap<>();
        this.dependencies = new HashMap<>();
    }

    public <Type> void bind(final Class<Type> type, final Type instance) {
        providers.put(type, context -> instance);
        dependencies.put(type, List.of());
    }

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementation) {
        check(implementation);
        Constructor<Implementation> injectConstructor = injectConstructor(implementation);
        providers.put(type, new ConstructorInjectionProvider<>(injectConstructor));
        dependencies.put(type, stream(injectConstructor.getParameters()).map(Parameter::getType).collect(Collectors.toList()));
    }

    public Context context() {
        dependencies.keySet().forEach(component -> checkDependencies(component, new Stack<>()));
        return new Context() {
            @Override
            @SuppressWarnings("unchecked")
            public <Type> Optional<Type> get(Class<Type> type) {
                return Optional.ofNullable(providers.get(type)).map(provider -> provider.get(this)).map(o -> (Type) o);
            }
        };
    }

    private <Type> void check(final Class<Type> implementation) {
        if (countOfInjectConstructors(implementation) > 1) throw new IllegalComponentException();
        if (countOfInjectConstructors(implementation) == 0 && noDefaultConstructor(implementation)) throw new IllegalComponentException();
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

    @SuppressWarnings("unchecked")
    private <Type> Constructor<Type> injectConstructor(final Class<Type> implementation) {
        Stream<Constructor<?>> injectConstructors = stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class));
        return (Constructor<Type>) injectConstructors.findFirst().orElseGet(() -> evaluate(implementation::getConstructor).evaluate());
    }

    private void checkDependencies(Class<?> component, Stack<Class<?>> visiting) {
        for (Class<?> dependency : dependencies.get(component)) {
            if (!dependencies.containsKey(dependency)) throw new DependencyNotFoundException(dependency, component);
            if (visiting.contains(dependency)) throw new CyclicDependenciesFoundException(visiting);
            visiting.push(dependency);
            checkDependencies(dependency, visiting);
            visiting.pop();
        }
    }

}
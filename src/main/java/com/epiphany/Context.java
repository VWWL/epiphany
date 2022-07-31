package com.epiphany;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    public <Type> Optional<Type> get(final Class<Type> type) {
        return (Optional<Type>) Optional.ofNullable(providers.get(type)).map(Provider::get);
    }

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementation) {
        check(implementation);
        providers.put(type, new ConstructorInjectionProvider<>(implementation));
    }

    class ConstructorInjectionProvider<Type> implements Provider<Type> {
        private final Class<Type> implementation;
        private boolean constructing;

        public ConstructorInjectionProvider(Class<Type> implementation) {
            this.implementation = implementation;
        }

        @Override
        public Type get() {
            if (constructing) throw new CyclicDependenciesFound();
            try {
                constructing();
                Constructor<Type> injectConstructor = injectConstructor(implementation);
                Object[] dependencies = stream(injectConstructor.getParameters())
                        .map(p -> Context.this.get(p.getType()).orElseThrow(DependencyNotFoundException::new))
                        .toArray(Object[]::new);
                return injectConstructor.newInstance(dependencies);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } finally {
                constructed();
            }
        }

        private void constructing() {
            this.constructing = true;
        }

        private void constructed() {
            this.constructing = false;
        }
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
}

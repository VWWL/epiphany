package com.epiphany.context;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class Context {
    private final Map<Class<?>, Provider<?>> providers;

    public Context() {
        this.providers = new HashMap<>();
    }

    public <Type> void bind(final Class<Type> type, final Type instance) {
        providers.put(type, () -> instance);
    }

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementation) {
        check(implementation);
        providers.put(type, new ConstructorInjectionProvider<>(type, implementation));
    }

    @SuppressWarnings("unchecked")
    public <Type> Optional<Type> get(final Class<Type> type) {
        return Optional.ofNullable(providers.get(type)).map(Provider::get).map(o -> (Type) o);
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

    class ConstructorInjectionProvider<Type> implements Provider<Type> {
        private final Class<?> componentType;
        private final Class<Type> implementation;
        private boolean constructing;

        public ConstructorInjectionProvider(final Class<?> componentType, final Class<Type> implementation) {
            this.componentType = componentType;
            this.implementation = implementation;
        }

        @Override
        public Type get() {
            if (constructing) throw new CyclicDependenciesFoundException(componentType);
            try {
                constructing();
                return createInstanceByInjectOrDefaultConstructor();
            } catch (CyclicDependenciesFoundException e) {
                throw new CyclicDependenciesFoundException(componentType, e);
            } finally {
                constructed();
            }
        }

        private Type createInstanceByInjectOrDefaultConstructor() {
            Constructor<Type> injectConstructor = injectConstructor(implementation);
            Object[] dependencies = stream(injectConstructor.getParameters())
                    .map(Parameter::getType)
                    .map(type -> Context.this.get(type).orElseThrow(() -> new DependencyNotFoundException(type, componentType)))
                    .toArray(Object[]::new);
            return evaluate(() -> injectConstructor.newInstance(dependencies)).evaluate();
        }

        private void constructing() {
            this.constructing = true;
        }

        private void constructed() {
            this.constructing = false;
        }
    }
}

package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        providers.put(type, new ConstructorInjectionProvider<>(type, implementation));
        dependencies.put(type, stream(injectConstructor(implementation).getParameters()).map(Parameter::getType).collect(Collectors.toList()));
    }

    public Context context() {
        for (Class<?> component : dependencies.keySet()) {
            for (Class<?> dependency : dependencies.get(component)) {
                if (!dependencies.containsKey(dependency)) throw new DependencyNotFoundException(dependency, component);
            }
        }
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

    class ConstructorInjectionProvider<Type> implements Provider<Type> {
        private final Class<?> componentType;
        private final Class<Type> implementation;
        private boolean constructing;

        public ConstructorInjectionProvider(final Class<?> componentType, final Class<Type> implementation) {
            this.componentType = componentType;
            this.implementation = implementation;
        }

        @Override
        public Type get(Context context) {
            if (constructing) throw new CyclicDependenciesFoundException(componentType);
            try {
                constructing();
                return createInstanceByInjectOrDefaultConstructor(context);
            } catch (CyclicDependenciesFoundException e) {
                throw new CyclicDependenciesFoundException(componentType, e);
            } finally {
                constructed();
            }
        }

        private Type createInstanceByInjectOrDefaultConstructor(Context context) {
            Constructor<Type> injectConstructor = injectConstructor(implementation);
            Object[] dependencies = stream(injectConstructor.getParameters())
                    .map(Parameter::getType)
                    .map(context::get)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
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

package com.epiphany.context;

import com.epiphany.context.exception.*;

import java.util.*;

public final class ContextConfig {

    private final InjectionProviders injectionProviders;

    public ContextConfig() {
        this.injectionProviders = new InjectionProviders();
    }

    public <Type> void bind(final Class<Type> type, final Type instance) {
        injectionProviders.providers().put(type, context -> instance);
    }

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementation) {
        injectionProviders.providers().put(type, new InjectionProvider<>(implementation));
    }

    public Context context() {
        injectionProviders.providers().keySet().forEach(component -> checkDependencies(component, new Stack<>()));
        return new Context() {
            @Override
            @SuppressWarnings("unchecked")
            public <Type> Optional<Type> get(Class<Type> type) {
                return Optional.ofNullable(injectionProviders.providers().get(type)).map(provider -> provider.get(this)).map(o -> (Type) o);
            }
        };
    }

    private void checkDependencies(final Class<?> component, final Stack<Class<?>> visiting) {
        for (Class<?> dependency : injectionProviders.providers().get(component).dependencies()) {
            if (!injectionProviders.providers().containsKey(dependency)) throw new DependencyNotFoundException(dependency, component);
            if (visiting.contains(dependency)) throw new CyclicDependenciesFoundException(visiting);
            visiting.push(dependency);
            checkDependencies(dependency, visiting);
            visiting.pop();
        }
    }

}

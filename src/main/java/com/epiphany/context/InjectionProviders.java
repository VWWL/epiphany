package com.epiphany.context;

import com.epiphany.context.exception.*;

import java.util.*;

import static com.epiphany.general.Exceptions.evaluate;

public class InjectionProviders {

    private final Map<Class<?>, Provider<?>> impl;

    public InjectionProviders() {
        this.impl = new HashMap<>();
    }

    public Map<Class<?>, Provider<?>> providers() {
        return impl;
    }

    public <Type> void register(Class<Type> type, Type instance) {
        impl.put(type, context -> instance);
    }

    public <Type, Implementation extends Type> void register(Class<Type> type, Class<Implementation> implementation) {
        impl.put(type, new InjectionProvider<>(implementation));
    }

    @SuppressWarnings("unchecked")
    public <Type, Implementation extends Type> void register(Class<Type> type, ClassName className) {
        try {
            Class<Implementation> implementation = (Class<Implementation>) evaluate(() -> Class.forName(className.className())).evaluate();
            if (!type.isAssignableFrom(implementation)) throw new IllegalComponentException();
            this.register(type, implementation);
        } catch (Exception e) {
            throw new IllegalComponentException();
        }
    }

    public void checkDependencies() {
        impl.keySet().forEach(component -> checkDependencies(component, new Stack<>()));
    }

    private void checkDependencies(final Class<?> component, final Stack<Class<?>> visiting) {
        for (Class<?> dependency : impl.get(component).dependencies()) {
            if (!impl.containsKey(dependency)) throw new DependencyNotFoundException(dependency, component);
            if (visiting.contains(dependency)) throw new CyclicDependenciesFoundException(visiting);
            visiting.push(dependency);
            this.checkDependencies(dependency, visiting);
            visiting.pop();
        }
    }

}

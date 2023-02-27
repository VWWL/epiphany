package com.epiphany.context;

import com.epiphany.context.exception.*;

import java.util.*;

public class InjectionProviders {

    private final Map<Class<?>, Provider<?>> impl;

    public InjectionProviders() {
        this.impl = new HashMap<>();
    }

    public <Type> Provider<?> get(Class<Type> type) {
        return impl.get(type);
    }

    public <Type> void register(Class<Type> type, Type instance) {
        impl.put(type, context -> instance);
    }

    public <Type, Implementation extends Type> void register(Class<Type> type, Class<Implementation> implementation) {
        impl.put(type, new GeneralInjectionProvider<>(implementation));
        if (!implementation.isAnnotationPresent(Injections.class)) return;
        InjectStream.of(implementation.getDeclaredMethods()).injectionPart().forEach(method -> impl.put(method.getReturnType(), new ExplicitInjectionProvider<>(type, method)));
    }

    public <Type, Implementation extends Type> void register(InjectClasses<Type, Implementation> injectClasses) {
        this.register(injectClasses.type(), injectClasses.implementation());
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

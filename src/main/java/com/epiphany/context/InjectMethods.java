package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.execute;
import static java.util.Arrays.stream;

class InjectMethods {

    private final List<Method> impl;

    public <Type> InjectMethods(final Class<Type> component) {
        this.impl = initInjectMethods(component);
        if (impl.stream().anyMatch(o -> o.getTypeParameters().length != 0)) throw new IllegalComponentException();
    }

    Stream<Class<?>> dependencies() {
        return impl.stream().flatMap(m -> stream(m.getParameterTypes()));
    }

    public <Type> void injectInto(final Context context, final Type instance) {
        for (Method method : impl) {
            method.setAccessible(true);
            execute(() -> method.invoke(instance, toDependencies(context, method))).run();
        }
    }

    @SuppressWarnings("all")
    public Object[] toDependencies(final Context context, final Executable executable) {
        return stream(executable.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
    }

    private <Type> List<Method> initInjectMethods(final Class<Type> component) {
        List<Method> methods = new Traverser<Method>().traverse(component, (m, current) -> InjectStream.of(current.getDeclaredMethods()).injectablePart()
            .filter(o -> isOverrideByInjectMethod(m, o))
            .filter(o -> isOverrideByNoInjectMethod(component, o))
            .toList());
        Collections.reverse(methods);
        return methods;
    }

    private <Type> boolean isOverrideByNoInjectMethod(final Class<Type> component, final Method method) {
        return InjectStream.of(component.getDeclaredMethods()).notInjectablePart().noneMatch(m -> isOverride(method, m));
    }

    private boolean isOverrideByInjectMethod(final List<Method> injectMethods, final Method method) {
        return injectMethods.stream().noneMatch(m -> isOverride(method, m));
    }

    private boolean isOverride(final Method one, final Method another) {
        return another.getName().equals(one.getName()) && Arrays.equals(another.getParameterTypes(), one.getParameterTypes());
    }

}

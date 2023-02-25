package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.Method;
import java.util.*;

public class InjectMethods {

    private final List<Method> impl;

    public <Type> InjectMethods(Class<Type> component) {
        this.impl = initInjectMethods(component);
        if (impl.stream().anyMatch(o -> o.getTypeParameters().length != 0)) throw new IllegalComponentException();
    }

    public List<Method> get() {
        return impl;
    }

    private static <Type> List<Method> initInjectMethods(Class<Type> component) {
        List<Method> methods = new Traverser<Method>().traverse(component, (methods1, current) -> InjectStream.of(current.getDeclaredMethods()).injectablePart()
            .filter(o -> isOverrideByInjectMethod(methods1, o))
            .filter(o -> isOverrideByNoInjectMethod(component, o))
            .toList());
        Collections.reverse(methods);
        return methods;
    }

    private static <Type> boolean isOverrideByNoInjectMethod(Class<Type> component, Method method) {
        return InjectStream.of(component.getDeclaredMethods()).notInjectablePart().noneMatch(m -> isOverride(method, m));
    }

    private static boolean isOverrideByInjectMethod(List<Method> injectMethods, Method method) {
        return injectMethods.stream().noneMatch(m -> isOverride(method, m));
    }

    private static boolean isOverride(Method first, Method another) {
        return another.getName().equals(first.getName()) && Arrays.equals(another.getParameterTypes(), first.getParameterTypes());
    }

}

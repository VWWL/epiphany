package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.*;

import static com.epiphany.general.Exceptions.execute;
import static java.util.Arrays.stream;

public class InjectMethods {

    private final List<Method> impl;

    public <Type> InjectMethods(Class<Type> component) {
        this.impl = initInjectMethods(component);
        if (impl.stream().anyMatch(o -> o.getTypeParameters().length != 0)) throw new IllegalComponentException();
    }

    public List<Method> get() {
        return impl;
    }

    public <Type> void injectInto(Context context, Type instance) {
        for (Method method : impl) {
            method.setAccessible(true);
            execute(() -> method.invoke(instance, toDependencies(context, method))).run();
        }
    }

    @SuppressWarnings("all")
    public static Object[] toDependencies(Context context, Executable executable) {
        return stream(executable.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
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

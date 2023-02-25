package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public final class ConstructorInjectionProvider<Type> implements Provider<Type> {

    private final Constructor<Type> injectConstructor;
    private final List<Field> injectFields;
    private final List<Method> injectMethods;

    public ConstructorInjectionProvider(final Class<Type> component) {
        if (Modifier.isAbstract(component.getModifiers())) throw new IllegalComponentException();
        this.injectConstructor = initInjectConstructor(component);
        this.injectFields = initInjectFields(component);
        this.injectMethods = initInjectMethods(component);
        if (injectFields.stream().anyMatch(o -> Modifier.isFinal(o.getModifiers()))) throw new IllegalComponentException();
        if (injectMethods.stream().anyMatch(o -> o.getTypeParameters().length != 0)) throw new IllegalComponentException();
    }

    @Override
    @SuppressWarnings("all")
    public Type get(final Context context) {
        Object[] dependencies = stream(injectConstructor.getParameters())
            .map(Parameter::getType)
            .map(context::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Object[]::new);
        return evaluate(() -> {
            Type instance = injectConstructor.newInstance(dependencies);
            for (Field field : injectFields) {
                field.setAccessible(true);
                field.set(instance, context.get(field.getType()).get());
            }
            for (Method method : injectMethods) {
                method.setAccessible(true);
                method.invoke(instance, stream(method.getParameterTypes()).map(t -> context.get(t)).map(o -> o.get()).toArray(Object[]::new));
            }
            return instance;
        }).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return Stream.of(
            injectFields.stream().map(Field::getType),
            injectMethods.stream().flatMap(m -> stream(m.getParameterTypes())),
            stream(injectConstructor.getParameters()).map(Parameter::getType)
        ).flatMap(o -> o).collect(Collectors.toList());
    }

    private static <Type> List<Field> initInjectFields(Class<Type> component) {
        List<Field> injectFields = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            injectFields.addAll(stream(current.getDeclaredFields()).filter(o -> o.isAnnotationPresent(Inject.class)).toList());
            current = current.getSuperclass();
        }
        return injectFields;
    }

    private static <Type> List<Method> initInjectMethods(Class<Type> component) {
        List<Method> injectMethods = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            List<Method> currentInjectMethods = stream(current.getDeclaredMethods())
                .filter(o -> o.isAnnotationPresent(Inject.class))
                .filter(o -> injectMethods.stream().noneMatch(m -> m.getName().equals(o.getName()) && Arrays.equals(m.getParameterTypes(), o.getParameterTypes())))
                .filter(o -> stream(component.getDeclaredMethods()).filter(m -> !m.isAnnotationPresent(Inject.class)).noneMatch(m -> m.getName().equals(o.getName()) && Arrays.equals(m.getParameterTypes(), o.getParameterTypes())))
                .toList();
            injectMethods.addAll(currentInjectMethods);
            current = current.getSuperclass();
        }
        Collections.reverse(injectMethods);
        return injectMethods;
    }

    @SuppressWarnings("unchecked")
    private static <Type> Constructor<Type> initInjectConstructor(Class<Type> component) {
        return (Constructor<Type>) stream(component.getConstructors())
            .filter(c -> c.isAnnotationPresent(Inject.class))
            .findFirst()
            .orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

}

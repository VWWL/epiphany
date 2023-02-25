package com.epiphany.context;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public final class InjectionProvider<Type> implements Provider<Type> {

    private final Constructor<Type> injectConstructor;
    private final List<Field> injectFields;
    private final List<Method> injectMethods;

    public InjectionProvider(final Class<Type> component) {
        checkConstructor(component);
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

    private <Type> void checkConstructor(final Class<Type> implementation) {
        if (noConstructor(implementation)) return;
        if (countOfInjectConstructors(implementation) > 1) throw new IllegalComponentException();
        if (countOfInjectConstructors(implementation) == 0 && noDefaultConstructor(implementation)) throw new IllegalComponentException();
    }

    private <Type> boolean noConstructor(Class<Type> implementation) {
        return stream(implementation.getConstructors()).findAny().isEmpty();
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

    private static <Type> List<Field> initInjectFields(Class<Type> component) {
        List<Field> injectFields = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            injectFields.addAll(injectableStream(current.getDeclaredFields()).toList());
            current = current.getSuperclass();
        }
        return injectFields;
    }

    private static <Type> List<Method> initInjectMethods(Class<Type> component) {
        List<Method> injectMethods = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            List<Method> currentInjectMethods = injectableStream(current.getDeclaredMethods())
                .filter(o -> injectMethods.stream().noneMatch(m -> isOverride(o, m)))
                .filter(o -> uninjectableStream(component.getDeclaredMethods()).noneMatch(m -> isOverride(o, m)))
                .toList();
            injectMethods.addAll(currentInjectMethods);
            current = current.getSuperclass();
        }
        Collections.reverse(injectMethods);
        return injectMethods;
    }

    @SuppressWarnings("unchecked")
    private static <Type> Constructor<Type> initInjectConstructor(Class<Type> component) {
        return (Constructor<Type>) injectableStream(component.getConstructors()).findFirst().orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

    private static boolean isOverride(Method first, Method another) {
        return another.getName().equals(first.getName()) && Arrays.equals(another.getParameterTypes(), first.getParameterTypes());
    }

    private static <T extends AnnotatedElement> Stream<T> injectableStream(T[] declaredFields) {
        return stream(declaredFields).filter(o -> o.isAnnotationPresent(Inject.class));
    }

    private static <T extends AnnotatedElement> Stream<T> uninjectableStream(T[] declaredFields) {
        return stream(declaredFields).filter(o -> !o.isAnnotationPresent(Inject.class));
    }

}

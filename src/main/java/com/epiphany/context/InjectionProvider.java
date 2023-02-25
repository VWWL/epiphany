package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;
import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public final class InjectionProvider<Type> implements Provider<Type> {

    private final Constructor<Type> injectConstructor;
    private final List<Field> injectFields;
    private final List<Method> injectMethods;

    public InjectionProvider(final Class<Type> component) {
        checkConstructor(component);
        this.injectConstructor = initInjectConstructor(component);
        this.injectFields = initInjectFields(component);
        this.injectMethods = initInjectMethods(component);
        if (injectFields.stream().anyMatch(o -> Modifier.isFinal(o.getModifiers()))) throw new IllegalComponentException();
        if (injectMethods.stream().anyMatch(o -> o.getTypeParameters().length != 0)) throw new IllegalComponentException();
    }

    @Override
    @SuppressWarnings("all")
    public Type get(final Context context) {
        Object[] dependencies = toDependencies(context, injectConstructor);
        return evaluate(() -> {
            Type instance = injectConstructor.newInstance(dependencies);
            for (Field field : injectFields) {
                field.setAccessible(true);
                field.set(instance, toDependency(context, field));
            }
            for (Method method : injectMethods) {
                method.setAccessible(true);
                method.invoke(instance, toDependencies(context, method));
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

    private <Type> void checkConstructor(final Class<Type> component) {
        if (Modifier.isAbstract(component.getModifiers())) throw new IllegalComponentException();
        if (noConstructor(component)) return;
        if (countOfInjectConstructors(component) > 1) throw new IllegalComponentException();
        if (countOfInjectConstructors(component) == 0 && noDefaultConstructor(component)) throw new IllegalComponentException();
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
        return traverse(component, (injectMethods1, current) -> injectableStream(current.getDeclaredFields()).toList());
    }

    private static <Type> List<Method> initInjectMethods(Class<Type> component) {
        List<Method> methods = traverse(component, (methods1, current) -> injectableStream(current.getDeclaredMethods())
            .filter(o -> isOverrideByInjectMethod(methods1, o))
            .filter(o -> isOverrideByNoInjectMethod(component, o))
            .toList());
        Collections.reverse(methods);
        return methods;
    }

    private static <Type> boolean isOverrideByNoInjectMethod(Class<Type> component, Method method) {
        return uninjectableStream(component.getDeclaredMethods()).noneMatch(m -> isOverride(method, m));
    }

    private static boolean isOverrideByInjectMethod(List<Method> injectMethods, Method method) {
        return injectMethods.stream().noneMatch(m -> isOverride(method, m));
    }

    @SuppressWarnings("unchecked")
    private static <Type> Constructor<Type> initInjectConstructor(Class<Type> component) {
        return (Constructor<Type>) injectableStream(component.getConstructors()).findFirst().orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

    @SuppressWarnings("all")
    private static Object toDependency(Context context, Field field) {
        return context.get(field.getType()).get();
    }

    @SuppressWarnings("all")
    public static Object[] toDependencies(Context context, Executable executable) {
        return stream(executable.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
    }

    private static <T> List<T> traverse(Class<?> component, BiFunction<List<T>, Class<?>, List<T>> finder) {
        List<T> members = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            members.addAll(finder.apply(members, current));
            current = current.getSuperclass();
        }
        return members;
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

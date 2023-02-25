package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public final class InjectionProvider<Type> implements Provider<Type> {

    private final InjectConstructor<Type> constructors;
    private final Fields injectFields;
    private final List<Method> injectMethods;

    public InjectionProvider(final Class<Type> component) {
        checkConstructor(component);
        this.constructors = new InjectConstructor<>(component);
        this.injectFields = new Fields(component);
        this.injectMethods = initInjectMethods(component);
        if (injectFields.injectFields.stream().anyMatch(o -> Modifier.isFinal(o.getModifiers()))) throw new IllegalComponentException();
        if (injectMethods.stream().anyMatch(o -> o.getTypeParameters().length != 0)) throw new IllegalComponentException();
    }

    @Override
    @SuppressWarnings("all")
    public Type get(final Context context) {
        return evaluate(() -> {
            Type instance = constructors.newInstance(context);
            for (Field field : injectFields.injectFields) {
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
            injectFields.injectFields.stream().map(Field::getType),
            injectMethods.stream().flatMap(m -> stream(m.getParameterTypes())),
            stream(constructors.dependencyClasses())
        ).flatMap(o -> o).collect(Collectors.toList());
    }

    private <Type> void checkConstructor(final Class<Type> component) {
        if (Modifier.isAbstract(component.getModifiers())) throw new IllegalComponentException();
        if (countOfInjectConstructors(component) > 1) throw new IllegalComponentException();
        if (countOfInjectConstructors(component) == 0 && noDefaultConstructor(component)) throw new IllegalComponentException();
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

    private static <Type> List<Method> initInjectMethods(Class<Type> component) {
        List<Method> methods = new Traverser<Method>().traverse(component, (methods1, current) -> injectableStream(current.getDeclaredMethods())
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

    @SuppressWarnings("all")
    private static Object toDependency(Context context, Field field) {
        return context.get(field.getType()).get();
    }

    @SuppressWarnings("all")
    public static Object[] toDependencies(Context context, Executable executable) {
        return stream(executable.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
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

package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public final class InjectionProvider<Type> implements Provider<Type> {

    private final InjectConstructor<Type> constructors;
    private final InjectFields injectFields;
    private final InjectMethods injectMethods;

    public InjectionProvider(final Class<Type> component) {
        checkConstructor(component);
        this.constructors = new InjectConstructor<>(component);
        this.injectFields = new InjectFields(component);
        this.injectMethods = new InjectMethods(component);
    }

    @Override
    @SuppressWarnings("all")
    public Type get(final Context context) {
        return evaluate(() -> {
            Type instance = constructors.newInstance(context);
            injectFields.injectInto(context, instance);
            for (Method method : injectMethods.get()) {
                method.setAccessible(true);
                method.invoke(instance, toDependencies(context, method));
            }
            return instance;
        }).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return Stream.of(
            injectFields.dependencies(),
            injectMethods.get().stream().flatMap(m -> stream(m.getParameterTypes())),
            constructors.dependencies()
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

    @SuppressWarnings("all")
    private static Object toDependency(Context context, Field field) {
        return context.get(field.getType()).get();
    }

    @SuppressWarnings("all")
    public static Object[] toDependencies(Context context, Executable executable) {
        return stream(executable.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
    }

}

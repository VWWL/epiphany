package com.epiphany.context;

import com.epiphany.context.exception.IllegalComponentException;

import java.lang.reflect.*;
import java.util.List;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public final class InjectionProvider<Type> implements Provider<Type> {

    private final InjectConstructor<Type> constructor;
    private final InjectFields injectFields;
    private final InjectMethods injectMethods;

    public InjectionProvider(final Class<Type> component) {
        checkConstructor(component);
        this.constructor = new InjectConstructor<>(component);
        this.injectFields = new InjectFields(component);
        this.injectMethods = new InjectMethods(component);
    }

    @Override
    @SuppressWarnings("all")
    public Type get(final Context context) {
        return evaluate(() -> constructor.newInstance(context, injectFields, injectMethods)).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return Stream.of(
            injectFields.dependencies(),
            injectMethods.dependencies(),
            constructor.dependencies()
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

}

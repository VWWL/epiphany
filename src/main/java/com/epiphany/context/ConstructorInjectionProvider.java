package com.epiphany.context;

import java.lang.reflect.*;
import java.util.Optional;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

class ConstructorInjectionProvider<Type> implements Provider<Type> {

    private final Class<?> componentType;
    private final Constructor<Type> injectConstructor;
    private boolean constructing;

    public ConstructorInjectionProvider(final Class<?> componentType, Constructor<Type> injectConstructor) {
        this.componentType = componentType;
        this.injectConstructor = injectConstructor;
    }

    @Override
    public Type get(Context context) {
        if (constructing) throw new CyclicDependenciesFoundException(componentType);
        try {
            constructing();
            return createInstanceByInjectOrDefaultConstructor(context);
        } catch (CyclicDependenciesFoundException e) {
            throw new CyclicDependenciesFoundException(componentType, e);
        } finally {
            constructed();
        }
    }

    private Type createInstanceByInjectOrDefaultConstructor(Context context) {
        Object[] dependencies = stream(injectConstructor.getParameters())
            .map(Parameter::getType)
            .map(context::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Object[]::new);
        return evaluate(() -> injectConstructor.newInstance(dependencies)).evaluate();
    }

    private void constructing() {
        this.constructing = true;
    }

    private void constructed() {
        this.constructing = false;
    }

}

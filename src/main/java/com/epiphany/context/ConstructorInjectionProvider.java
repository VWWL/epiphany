package com.epiphany.context;

import java.lang.reflect.*;
import java.util.Optional;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

class ConstructorInjectionProvider<Type> implements Provider<Type> {

    private final Constructor<Type> injectConstructor;

    public ConstructorInjectionProvider(Constructor<Type> injectConstructor) {
        this.injectConstructor = injectConstructor;
    }

    @Override
    public Type get(Context context) {
        Object[] dependencies = stream(injectConstructor.getParameters())
            .map(Parameter::getType)
            .map(context::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Object[]::new);
        return evaluate(() -> injectConstructor.newInstance(dependencies)).evaluate();
    }

}

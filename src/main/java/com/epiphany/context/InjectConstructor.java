package com.epiphany.context;

import java.lang.reflect.*;
import java.util.Optional;
import java.util.stream.Stream;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class InjectConstructor<Type> {

    private final Constructor<Type> impl;

    @SuppressWarnings("unchecked")
    public InjectConstructor(Class<Type> component) {
        this.impl = (Constructor<Type>) injectableStream(component.getConstructors()).findFirst().orElseGet(() -> evaluate(component::getDeclaredConstructor).evaluate());
    }

    private static <T extends AnnotatedElement> Stream<T> injectableStream(T[] declaredFields) {
        return stream(declaredFields).filter(o -> o.isAnnotationPresent(Inject.class));
    }

    public Class<?>[] dependencyClasses() {
        return impl.getParameterTypes();
    }

    @SuppressWarnings("all")
    Type newInstance(Context context) {
        Object[] dependencies = stream(impl.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
        return evaluate(() -> impl.newInstance(dependencies)).evaluate();
    }

}

package com.epiphany.context;

import java.lang.reflect.*;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class Fields {

    final List<Field> injectFields;

    public <Type> Fields(Class<Type> component) {
        this.injectFields = initInjectFields(component);
    }

    private static <Type> List<Field> initInjectFields(Class<Type> component) {
        return new Traverser<Field>().traverse(component, (injectMethods1, current) -> injectableStream(current.getDeclaredFields()).toList());
    }

    private static <T extends AnnotatedElement> Stream<T> injectableStream(T[] declaredFields) {
        return stream(declaredFields).filter(o -> o.isAnnotationPresent(Inject.class));
    }

}

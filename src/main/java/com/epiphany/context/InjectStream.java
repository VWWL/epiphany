package com.epiphany.context;

import java.lang.reflect.AnnotatedElement;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

final class InjectStream<T extends AnnotatedElement> {

    private final T[] content;

    public static <T extends AnnotatedElement> InjectStream<T> of(final T[] content) {
        return new InjectStream<>(content);
    }

    private InjectStream(final T[] content) {
        this.content = content;
    }

    public Stream<T> injectablePart() {
        return stream(content).filter(o -> o.isAnnotationPresent(Inject.class));
    }

    public Stream<T> notInjectablePart() {
        return stream(content).filter(o -> !o.isAnnotationPresent(Inject.class));
    }

    public Stream<T> injectionPart() {
        return stream(content).filter(o -> o.isAnnotationPresent(Injection.class));
    }

}

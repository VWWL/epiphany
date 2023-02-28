package com.epiphany.context;

import java.util.*;
import java.util.function.BiFunction;

class Traverser<T> {

    public List<T> traverse(final Class<?> component, final BiFunction<List<T>, Class<?>, List<T>> finder) {
        List<T> members = new ArrayList<>();
        Class<?> current = component;
        while (current != Object.class) {
            members.addAll(finder.apply(members, current));
            current = current.getSuperclass();
        }
        return members;
    }

}

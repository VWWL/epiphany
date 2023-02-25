package com.epiphany.context;

import java.util.List;

public interface Provider<Type> {

    Type get(Context context);

    default List<Class<?>> dependencies() {
        return List.of();
    }

}

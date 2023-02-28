package com.epiphany.context;

import java.util.List;

interface Provider<Type> {

    Type get(Context context);

    default List<Class<?>> dependencies() {
        return List.of();
    }

}

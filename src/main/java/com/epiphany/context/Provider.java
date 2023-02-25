package com.epiphany.context;

public interface Provider<Type> {
    Type get(Context context);
}

package com.epiphany.context;

import com.epiphany.InjectionProvider;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public interface Context {

    <Type> Optional<Type> get(final Class<Type> type);

    Optional<InjectionProvider> get(ParameterizedType type);

}

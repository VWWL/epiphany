package com.epiphany.context;

import com.epiphany.context.exception.DependencyNotFoundException;

import java.lang.reflect.Method;
import java.util.*;

import static com.epiphany.general.Exceptions.evaluate;
import static java.util.Arrays.stream;

public class ExplicitRegistrationProvider<RegistrationsType, Type> implements Provider<Type> {

    private final Class<RegistrationsType> registrationsType;
    private final Method method;

    public ExplicitRegistrationProvider(Class<RegistrationsType> registrationsType, Method method) {
        this.registrationsType = registrationsType;
        this.method = method;
    }

    @Override
    @SuppressWarnings("all")
    public Type get(Context context) {
        RegistrationsType registrations = context.get(registrationsType).get();
        Object[] dependencies = stream(method.getParameters())
            .map(o -> context.get(o.getType()).orElseThrow(() -> new DependencyNotFoundException(o.getType(), method.getReturnType())))
            .toArray(Object[]::new);
        return (Type) evaluate(() -> method.invoke(registrations, dependencies)).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return Arrays.stream(method.getParameterTypes()).toList();
    }

}

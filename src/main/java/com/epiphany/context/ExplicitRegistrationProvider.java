package com.epiphany.context;

import java.lang.reflect.*;
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
        Object[] dependencies = stream(method.getParameters()).map(Parameter::getType).map(context::get).map(Optional::get).toArray(Object[]::new);
        return (Type) evaluate(() -> method.invoke(registrations, dependencies)).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return Arrays.stream(method.getParameterTypes()).toList();
    }

}

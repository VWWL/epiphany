package com.epiphany.context;

import java.util.List;
import java.util.stream.*;

import static com.epiphany.general.Exceptions.evaluate;

final class GeneralInjectionProvider<Type> implements Provider<Type> {

    private final InjectConstructor<Type> constructor;
    private final InjectFields injectFields;
    private final InjectMethods injectMethods;

    public GeneralInjectionProvider(final Class<Type> component) {
        this.constructor = new InjectConstructor<>(component);
        this.injectFields = new InjectFields(component);
        this.injectMethods = new InjectMethods(component);
    }

    @Override
    @SuppressWarnings("all")
    public Type get(final Context context) {
        return evaluate(() -> constructor.newInstance(context, injectFields, injectMethods)).evaluate();
    }

    @Override
    public List<Class<?>> dependencies() {
        return Stream.of(injectFields.dependencies(), injectMethods.dependencies(), constructor.dependencies()).flatMap(o -> o).collect(Collectors.toList());
    }

}

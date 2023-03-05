package com.epiphany.context;

import com.epiphany.InjectionProvider;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

class GeneralContext implements Context {

    private final InjectionProviders injectionProviders;

    public GeneralContext(final InjectionProviders injectionProviders) {
        injectionProviders.checkDependencies();
        this.injectionProviders = injectionProviders;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type> Optional<Type> get(final Class<Type> type) {
        return Optional.ofNullable(injectionProviders.get(type)).map(provider -> provider.get(this)).map(o -> (Type) o);
    }

    @Override
    public Optional<InjectionProvider> get(ParameterizedType type) {
        if (type.getRawType() != Provider.class) return Optional.empty();
        Class<?> componentType = (Class<?>) type.getActualTypeArguments()[0];
        return Optional.ofNullable(injectionProviders.get(componentType)).map(o -> () -> o.get(this));
    }

}

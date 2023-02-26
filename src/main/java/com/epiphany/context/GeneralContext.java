package com.epiphany.context;

import java.util.Optional;

class GeneralContext implements Context {

    private final InjectionProviders injectionProviders;

    public GeneralContext(InjectionProviders injectionProviders) {
        injectionProviders.checkDependencies();
        this.injectionProviders = injectionProviders;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type> Optional<Type> get(Class<Type> type) {
        return Optional.ofNullable(injectionProviders.get(type)).map(provider -> provider.get(this)).map(o -> (Type) o);
    }

}

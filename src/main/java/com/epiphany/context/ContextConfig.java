package com.epiphany.context;

import java.util.Optional;

public final class ContextConfig {

    private final InjectionProviders injectionProviders;

    public ContextConfig() {
        this.injectionProviders = new InjectionProviders();
    }

    public <Type> void bind(final Class<Type> type, final Type instance) {
        injectionProviders.register(type, instance);
    }

    public <Type, Implementation extends Type> void bind(final Class<Type> type, final Class<Implementation> implementation) {
        injectionProviders.register(type, implementation);
    }

    public Context context() {
        injectionProviders.checkDependencies();
        return new Context() {
            @Override
            @SuppressWarnings("unchecked")
            public <Type> Optional<Type> get(Class<Type> type) {
                return Optional.ofNullable(injectionProviders.providers().get(type)).map(provider -> provider.get(this)).map(o -> (Type) o);
            }
        };
    }

}

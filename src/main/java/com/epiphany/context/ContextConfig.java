package com.epiphany.context;

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

    public <Type> void bind(Class<Type> type, ClassName className) {
        injectionProviders.register(type, className);
    }

    public Context context() {
        return new GeneralContext(injectionProviders);
    }

}

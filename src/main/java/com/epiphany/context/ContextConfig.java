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

    public <Type, Implementation extends Type> void bind(InjectClasses<Type, Implementation> injectClasses) {
        injectionProviders.register(injectClasses);
    }

    public Context context() {
        return new GeneralContext(injectionProviders);
    }

}

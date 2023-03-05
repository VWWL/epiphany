package com.epiphany.context.source;

import com.epiphany.InjectionProvider;
import com.epiphany.context.Inject;

public class ProviderInjectConstructor {

    InjectionProvider<Dependency> provider;

    @Inject
    public ProviderInjectConstructor(InjectionProvider<Dependency> provider) {
        this.provider = provider;
    }

    public InjectionProvider<Dependency> provider() {
        return provider;
    }

}

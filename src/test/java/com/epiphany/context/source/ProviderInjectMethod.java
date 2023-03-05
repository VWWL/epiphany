package com.epiphany.context.source;

import com.epiphany.InjectionProvider;
import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class ProviderInjectMethod {

    InjectionProvider<Dependency> provider;

    private @Inject void injectProvider(InjectionProvider<Dependency> provider) {
        this.provider = provider;
    }

    public InjectionProvider<Dependency> provider() {
        return provider;
    }

}

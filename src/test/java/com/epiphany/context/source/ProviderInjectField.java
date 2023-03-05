package com.epiphany.context.source;

import com.epiphany.InjectionProvider;
import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class ProviderInjectField {

    private @Inject InjectionProvider<Dependency> provider;

    public InjectionProvider<Dependency> provider() {
        return provider;
    }

}

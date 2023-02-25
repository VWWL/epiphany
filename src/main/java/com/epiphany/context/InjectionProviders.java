package com.epiphany.context;

import java.util.*;

public class InjectionProviders {

    private final Map<Class<?>, Provider<?>> providers;

    public InjectionProviders() {
        this.providers = new HashMap<>();
    }

    public Map<Class<?>, Provider<?>> providers() {
        return providers;
    }

}

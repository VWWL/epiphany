package com.epiphany.context.source;

import jakarta.inject.Inject;

public class ComponentWithFieldInjection {

    private @Inject Dependency dependency;

    public Dependency dependency() {
        return dependency;
    }

}

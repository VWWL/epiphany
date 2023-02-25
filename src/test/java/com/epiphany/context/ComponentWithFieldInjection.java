package com.epiphany.context;

import jakarta.inject.Inject;

class ComponentWithFieldInjection {

    private @Inject Dependency dependency;

    public Dependency dependency() {
        return dependency;
    }

}

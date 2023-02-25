package com.epiphany.context.source;

import com.epiphany.context.Inject;

public class ConstructorInjection implements Something {

    private final Dependency dependency;

    @Inject
    public ConstructorInjection(Dependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public Dependency dependency() {
        return dependency;
    }

}

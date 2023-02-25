package com.epiphany.context.source;

import jakarta.inject.Inject;

public class FieldInjection implements Something {

    private @Inject Dependency dependency;

    @Override
    public Dependency dependency() {
        return dependency;
    }

}

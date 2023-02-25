package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class FieldInjection implements Something {

    private @Inject Dependency dependency;

    @Override
    public Dependency dependency() {
        return dependency;
    }

}

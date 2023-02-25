package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class ComponentWithFieldInjection {

    private @Inject Dependency dependency;

    public Dependency dependency() {
        return dependency;
    }

}

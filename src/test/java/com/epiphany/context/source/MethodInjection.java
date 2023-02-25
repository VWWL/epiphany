package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class MethodInjection implements Something {

    private Dependency dependency;

    @Inject
    private void injectDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public Dependency dependency() {
        return dependency;
    }

}

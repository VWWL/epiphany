package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class SuperClassWithInjectMethod {

    private Dependency dependency;

    @Inject
    public void inject(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class SuperClassWithInjectMethod {

    private Dependency dependency;

    @Inject
    public void inject(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

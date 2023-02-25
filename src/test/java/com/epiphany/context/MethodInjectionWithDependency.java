package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class MethodInjectionWithDependency {

    private Dependency dependency;

    @Inject
    private void injectDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

package com.epiphany.context;

import jakarta.inject.Inject;

class DependencyWithInjectConstructor implements Dependency {

    private final String dependency;

    public @Inject DependencyWithInjectConstructor(final String dependency) {
        this.dependency = dependency;
    }

    public String dependency() {
        return dependency;
    }

}

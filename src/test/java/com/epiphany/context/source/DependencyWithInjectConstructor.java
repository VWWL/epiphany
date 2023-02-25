package com.epiphany.context.source;

import jakarta.inject.Inject;

public class DependencyWithInjectConstructor implements Dependency {

    private final String dependency;

    public @Inject DependencyWithInjectConstructor(final String dependency) {
        this.dependency = dependency;
    }

    public String dependency() {
        return dependency;
    }

}

package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class DependencyWithInjectConstructor implements Dependency {

    public @Inject DependencyWithInjectConstructor(final String dependency) {
    }

}

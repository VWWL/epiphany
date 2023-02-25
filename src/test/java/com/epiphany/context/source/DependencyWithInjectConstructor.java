package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class DependencyWithInjectConstructor implements Dependency {

    public @Inject DependencyWithInjectConstructor(final String dependency) {
    }

}

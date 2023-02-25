package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class DependencyWithNestedDependency implements Dependency {

    public @Inject DependencyWithNestedDependency(NestedDependency dependency) {
    }

}

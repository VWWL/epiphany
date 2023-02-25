package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class DependencyWithNestedDependency implements Dependency {

    public @Inject DependencyWithNestedDependency(NestedDependency dependency) {
    }

}

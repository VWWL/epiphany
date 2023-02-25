package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class DependencyWithNestedDependency implements Dependency {

    public @Inject DependencyWithNestedDependency(NestedDependency dependency) {
    }

}

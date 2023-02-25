package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicDependencyInjectConstructor implements Dependency {

    @Inject
    public IndirectCyclicDependencyInjectConstructor(AnotherDependency dependency) {
    }

}

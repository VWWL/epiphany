package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicDependencyInjectMethod implements Dependency {

    @Inject
    private void inject(AnotherDependency dependency) {
    }

}

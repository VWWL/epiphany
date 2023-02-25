package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicDependencyInjectConstructor implements Dependency {

    @Inject
    public IndirectCyclicDependencyInjectConstructor(AnotherDependency dependency) {
    }

}

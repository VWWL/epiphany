package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicDependencyInjectMethod implements Dependency {

    @Inject
    private void inject(AnotherDependency dependency) {
    }

}

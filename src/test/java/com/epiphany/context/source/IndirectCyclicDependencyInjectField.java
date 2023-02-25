package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicDependencyInjectField implements Dependency {

    private @Inject AnotherDependency dependency;

}

package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class CyclicDependencyInjectConstructor implements Dependency {

    @Inject
    public CyclicDependencyInjectConstructor(Dependency dependency) {
    }

}

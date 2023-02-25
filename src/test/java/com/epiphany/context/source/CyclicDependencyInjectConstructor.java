package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class CyclicDependencyInjectConstructor implements Dependency {

    @Inject
    public CyclicDependencyInjectConstructor(Dependency dependency) {
    }

}

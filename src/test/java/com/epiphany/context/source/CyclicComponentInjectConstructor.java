package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class CyclicComponentInjectConstructor implements Component {

    @Inject
    public CyclicComponentInjectConstructor(Dependency dependency) {
    }

}

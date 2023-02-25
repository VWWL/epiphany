package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class CyclicComponentInjectMethod implements Component {

    @Inject
    private void install(Dependency dependency) {
    }

}

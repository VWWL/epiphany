package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class MissingDependencyConstructor implements Component {

    @Inject
    public MissingDependencyConstructor(Dependency dependency) {
    }

}

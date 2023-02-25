package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class MissingDependencyMethod implements Component {

    @Inject
    private void injectDependency(Dependency dependency) {
    }

}

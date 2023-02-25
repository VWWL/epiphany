package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class DependencyDependedOnComponent implements Dependency {

    public @Inject DependencyDependedOnComponent(final Component component) {
    }

}

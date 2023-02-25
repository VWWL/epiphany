package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class DependencyDependedOnComponent implements Dependency {

    public @Inject DependencyDependedOnComponent(final Component component) {
    }

}

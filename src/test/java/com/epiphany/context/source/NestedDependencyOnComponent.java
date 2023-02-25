package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class NestedDependencyOnComponent implements NestedDependency {

    public @Inject NestedDependencyOnComponent(final Component component) {
    }

}

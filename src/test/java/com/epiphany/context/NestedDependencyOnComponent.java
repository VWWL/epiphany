package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class NestedDependencyOnComponent implements NestedDependency {

    public @Inject NestedDependencyOnComponent(final Component component) {
    }

}

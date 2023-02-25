package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class NestedDependencyOnComponent implements NestedDependency {

    public @Inject NestedDependencyOnComponent(final Component component) {
    }

}

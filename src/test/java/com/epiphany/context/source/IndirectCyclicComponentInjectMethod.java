package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicComponentInjectMethod implements AnotherDependency {

    @Inject
    private void inject(Component component) {
    }

}

package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicComponentInjectConstructor implements AnotherDependency {

    @Inject
    public IndirectCyclicComponentInjectConstructor(Component component) {
    }

}

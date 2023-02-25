package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicComponentInjectConstructor implements AnotherDependency {

    @Inject
    public IndirectCyclicComponentInjectConstructor(Component component) {
    }

}

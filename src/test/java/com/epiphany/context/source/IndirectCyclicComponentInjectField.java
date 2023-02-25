package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicComponentInjectField implements AnotherDependency {

    private @Inject Component component;

}

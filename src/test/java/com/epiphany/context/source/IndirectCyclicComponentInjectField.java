package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicComponentInjectField implements AnotherDependency {

    private @Inject Component component;

}

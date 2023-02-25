package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class IndirectCyclicComponentInjectMethod implements AnotherDependency {

    @Inject
    private void inject(Component component) {
    }

}

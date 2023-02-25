package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class CyclicComponentInjectMethod implements Component {

    @Inject
    private void install(Dependency dependency) {
    }

}

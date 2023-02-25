package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class CyclicComponentInjectConstructor implements Component {

    @Inject
    public CyclicComponentInjectConstructor(Dependency dependency) {
    }

}

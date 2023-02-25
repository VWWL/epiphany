package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class CyclicDependencyInjectMethod implements Component {

    @Inject
    private void install(Component component) {
    }

}

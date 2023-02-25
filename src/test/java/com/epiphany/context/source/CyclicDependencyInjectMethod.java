package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class CyclicDependencyInjectMethod implements Component {

    @Inject
    private void install(Component component) {
    }

}

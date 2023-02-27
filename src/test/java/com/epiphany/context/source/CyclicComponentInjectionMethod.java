package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Injections class CyclicComponentInjectionMethod implements Component {

    public @Injection Component component(Dependency dependency) {
        return new Component() {};
    }

}

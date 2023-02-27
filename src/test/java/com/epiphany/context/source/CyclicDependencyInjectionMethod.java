package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Injections class CyclicDependencyInjectionMethod implements Component {

    public @Injection Dependency component(Component component) {
        return new Dependency() {};
    }

}

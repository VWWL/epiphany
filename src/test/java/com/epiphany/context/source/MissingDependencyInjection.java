package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Injections class MissingDependencyInjection implements Component {

    public @Injection Component install(Dependency dependency) {
        return new Component() {};
    }

}

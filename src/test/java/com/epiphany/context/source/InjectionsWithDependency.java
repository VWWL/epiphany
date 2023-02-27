package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Injections class InjectionsWithDependency {

    public @Injection Dependency dependency(Component component) {
        return new Dependency() {};
    }

}

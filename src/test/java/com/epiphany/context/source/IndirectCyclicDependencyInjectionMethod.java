package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Injections class IndirectCyclicDependencyInjectionMethod implements Dependency {

    public @Injection Dependency dependency(AnotherDependency anotherDependency) {
        return new Dependency() {};
    }

}

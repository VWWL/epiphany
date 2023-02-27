package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Injections class IndirectCyclicComponentInjectionMethod implements AnotherDependency {

    public @Injection AnotherDependency anotherDependency(Component component) {
        return new AnotherDependency() {};
    }

}

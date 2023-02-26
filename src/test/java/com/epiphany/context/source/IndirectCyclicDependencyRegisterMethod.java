package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Registrations class IndirectCyclicDependencyRegisterMethod implements Dependency {

    public @Registration Dependency dependency(AnotherDependency anotherDependency) {
        return new Dependency() {};
    }

}

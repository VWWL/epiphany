package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Registrations class IndirectCyclicComponentRegisterMethod implements AnotherDependency {

    public @Registration AnotherDependency anotherDependency(Component component) {
        return new AnotherDependency() {};
    }

}

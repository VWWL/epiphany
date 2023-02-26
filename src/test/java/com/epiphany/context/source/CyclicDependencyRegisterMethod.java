package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Registrations class CyclicDependencyRegisterMethod implements Component {

    public @Registration Dependency component(Component component) {
        return new Dependency() {};
    }

}

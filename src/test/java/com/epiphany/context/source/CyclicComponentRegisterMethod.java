package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Registrations class CyclicComponentRegisterMethod implements Component {

    public @Registration Component component(Dependency dependency) {
        return new Component() {};
    }

}

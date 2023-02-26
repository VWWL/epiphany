package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Registrations class MissingDependencyRegistration implements Component {

    public @Registration Component install(Dependency dependency) {
        return new Component() {};
    }

}

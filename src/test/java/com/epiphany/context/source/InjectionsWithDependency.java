package com.epiphany.context.source;

import com.epiphany.context.*;

@SuppressWarnings("unused")
public @Registrations class InjectionsWithDependency {

    public @Registration Dependency dependency(Component component) {
        return new Dependency() {};
    }

}

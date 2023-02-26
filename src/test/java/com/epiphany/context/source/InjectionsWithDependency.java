package com.epiphany.context.source;

import com.epiphany.context.*;

public @Bindings class InjectionsWithDependency {

    public @Binding Dependency dependency() {
        return new Dependency() {};
    }

}

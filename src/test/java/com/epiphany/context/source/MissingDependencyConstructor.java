package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class MissingDependencyConstructor implements Component {

    @Inject
    public MissingDependencyConstructor(Dependency dependency) {
    }

}

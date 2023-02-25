package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class MissingDependencyMethod implements Component {

    @Inject
    private void injectDependency(Dependency dependency) {
    }

}

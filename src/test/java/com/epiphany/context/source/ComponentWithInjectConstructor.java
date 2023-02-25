package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class ComponentWithInjectConstructor implements Component {

    public @Inject ComponentWithInjectConstructor(final Dependency dependency) {
    }

}

package com.epiphany.context.source;

import jakarta.inject.Inject;

public class ComponentWithInjectConstructor implements Component {

    private final Dependency dependency;

    public @Inject ComponentWithInjectConstructor(final Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

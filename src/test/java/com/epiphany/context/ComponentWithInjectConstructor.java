package com.epiphany.context;

import jakarta.inject.Inject;

class ComponentWithInjectConstructor implements Component {

    private final Dependency dependency;

    public @Inject ComponentWithInjectConstructor(final Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

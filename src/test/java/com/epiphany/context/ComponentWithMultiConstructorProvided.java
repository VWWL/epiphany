package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class ComponentWithMultiConstructorProvided implements Component {

    public @Inject ComponentWithMultiConstructorProvided(final String name, final Double value) {
    }

    public @Inject ComponentWithMultiConstructorProvided(final String name) {
    }

}

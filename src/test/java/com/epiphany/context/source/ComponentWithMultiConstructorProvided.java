package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class ComponentWithMultiConstructorProvided implements Component {

    public @Inject ComponentWithMultiConstructorProvided(final String name, final Double value) {
    }

    public @Inject ComponentWithMultiConstructorProvided(final String name) {
    }

}

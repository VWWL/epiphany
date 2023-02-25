package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class ComponentWithMultiConstructorProvided implements Component {

    public @Inject ComponentWithMultiConstructorProvided(final String name, final Double value) {
    }

    public @Inject ComponentWithMultiConstructorProvided(final String name) {
    }

}

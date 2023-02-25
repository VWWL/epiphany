package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class ComponentWithInjectConstructor implements Component {

    public @Inject ComponentWithInjectConstructor(final Dependency dependency) {
    }

}

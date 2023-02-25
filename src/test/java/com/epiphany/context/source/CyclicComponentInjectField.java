package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class CyclicComponentInjectField implements Component {

    private @Inject Dependency dependency;

}

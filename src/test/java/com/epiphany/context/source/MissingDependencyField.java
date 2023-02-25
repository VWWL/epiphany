package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class MissingDependencyField implements Component {
    private @Inject Dependency dependency;
}

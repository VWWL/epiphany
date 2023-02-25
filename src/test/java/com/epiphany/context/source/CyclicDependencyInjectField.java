package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class CyclicDependencyInjectField implements Component {

    private @Inject Component component;

}

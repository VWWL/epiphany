package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class CyclicComponentInjectField implements Component {

    private @Inject Dependency dependency;

}

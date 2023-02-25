package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class CyclicDependencyInjectField implements Component {

    private @Inject Component component;

}

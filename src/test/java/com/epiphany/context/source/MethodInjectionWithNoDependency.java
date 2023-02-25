package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class MethodInjectionWithNoDependency {

    private int called = 0;

    @Inject
    private void install() {
        called++;
    }

    public int called() {
        return called;
    }

}

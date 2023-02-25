package com.epiphany.context.source;

import jakarta.inject.Inject;

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

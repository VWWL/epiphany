package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class MethodInjectionWithNoDependency {

    private int called = 0;

    @Inject
    private void install() {
        called++;
    }

    public int called() {
        return called;
    }

}

package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class CompareInjectMethodOverride {

    private int methodWithSameNameAndParameters;
    private int methodWithParametersNotSame;
    private int methodWithNameNotSame;

    @Inject
    public void methodWithSameNameAndParameters(Component p1) {
        methodWithSameNameAndParameters++;
    }

    @Inject
    public void methodWithParametersNotSame(Component p1, Dependency p2, Component p3) {
        methodWithParametersNotSame++;
    }

    @Inject
    public void methodWithNameNotSame(Component p1) {
        methodWithNameNotSame++;
    }

    public int methodWithSameNameAndParameters() {
        return methodWithSameNameAndParameters;
    }

    public int methodWithParametersNotSame() {
        return methodWithParametersNotSame;
    }

    public int methodWithNameNotSame() {
        return methodWithNameNotSame;
    }

}

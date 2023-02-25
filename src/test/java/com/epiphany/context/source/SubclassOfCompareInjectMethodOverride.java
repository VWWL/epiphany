package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class SubclassOfCompareInjectMethodOverride extends CompareInjectMethodOverride {

    @Inject
    @Override
    public void methodWithSameNameAndParameters(Component p1) {
    }

    @Inject
    public void methodWithParametersNotSame(Dependency p1, Component p2, Component p3) {
    }

    public void methodWithParametersNotSame(Component p1, Component p2, Dependency p3) {
    }

    @Inject
    public void theNotSameMethodOfMethodWithNameNotSame(Component p1) {
    }

    public void theNotSameMethodOfMethodWithNameNotSame2(Component p1) {
    }

}

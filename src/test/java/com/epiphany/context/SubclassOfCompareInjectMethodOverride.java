package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class SubclassOfCompareInjectMethodOverride extends CompareInjectMethodOverride {

    @Inject
    @Override
    public void methodWithSameNameAndParameters(String p1) {
    }

    @Inject
    public void methodWithParametersNotSame(Dependency p1, String p2, String p3) {
    }

    public void methodWithParametersNotSame(String p1, String p2, Dependency p3) {
    }

    @Inject
    public void theNotSameMethodOfMethodWithNameNotSame(String p1) {
    }

    public void theNotSameMethodOfMethodWithNameNotSame2(String p1) {
    }

}

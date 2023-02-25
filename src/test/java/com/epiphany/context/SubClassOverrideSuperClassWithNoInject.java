package com.epiphany.context;

class SubClassOverrideSuperClassWithNoInject extends SuperClassWithInjectMethod {

    @Override
    public void inject(Dependency dependency) {
        super.inject(dependency);
    }

}

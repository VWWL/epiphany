package com.epiphany.context.source;

public class SubClassOverrideSuperClassWithNoInject extends SuperClassWithInjectMethod {

    @Override
    public void inject(Dependency dependency) {
        super.inject(dependency);
    }

}

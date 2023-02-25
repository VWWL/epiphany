package com.epiphany.context.source;

import jakarta.inject.Inject;

public class SubClassOverrideSuperClassWithInject extends SuperClassWithInjectMethod {

    @Inject
    @Override
    public void inject(Dependency dependency) {
    }

}

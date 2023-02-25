package com.epiphany.context;

import jakarta.inject.Inject;

class SubClassOverrideSuperClassWithInject extends SuperClassWithInjectMethod {

    @Inject
    @Override
    public void inject(Dependency dependency) {
    }

}

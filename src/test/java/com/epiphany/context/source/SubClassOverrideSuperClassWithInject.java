package com.epiphany.context.source;

import com.epiphany.context.Inject;

public class SubClassOverrideSuperClassWithInject extends SuperClassWithInjectMethod {

    @Inject
    @Override
    public void inject(Dependency dependency) {
    }

}

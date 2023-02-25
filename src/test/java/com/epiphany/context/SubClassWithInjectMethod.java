package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class SubClassWithInjectMethod extends SuperClassWithInjectMethod {

    private Component component;

    @Inject
    private void injectComponent(Component component) {
        this.component = component;
    }

    public Component component() {
        return component;
    }

}

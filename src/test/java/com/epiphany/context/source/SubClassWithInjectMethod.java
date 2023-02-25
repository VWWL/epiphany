package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class SubClassWithInjectMethod extends SuperClassWithInjectMethod {

    private Component component;

    @Inject
    private void injectComponent(Component component) {
        this.component = component;
    }

    public Component component() {
        return component;
    }

}

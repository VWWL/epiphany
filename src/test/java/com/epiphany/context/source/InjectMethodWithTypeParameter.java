package com.epiphany.context.source;

import com.epiphany.context.Inject;

@SuppressWarnings("unused")
public class InjectMethodWithTypeParameter {

    @Inject
    <T> void install() {
    }

}


package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class InjectMethodWithTypeParameter {

    @Inject
    <T> void install() {
    }

}


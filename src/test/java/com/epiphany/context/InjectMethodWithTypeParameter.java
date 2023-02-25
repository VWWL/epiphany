package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class InjectMethodWithTypeParameter {

    @Inject
    <T> void install() {
    }

}


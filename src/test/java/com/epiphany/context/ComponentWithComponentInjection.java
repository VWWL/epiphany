package com.epiphany.context;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
class ComponentWithComponentInjection {

    private @Inject ComponentWithComponentInjection component;

}

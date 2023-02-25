package com.epiphany.context.source;

import jakarta.inject.Inject;

@SuppressWarnings("unused")
public class ComponentWithComponentInjection {

    private @Inject ComponentWithComponentInjection component;

}

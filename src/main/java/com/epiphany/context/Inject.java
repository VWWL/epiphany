package com.epiphany.context;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, CONSTRUCTOR, FIELD})
@Retention(RUNTIME)
public @interface Inject {
}

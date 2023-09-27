package io.rtr.alchemy.identities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotates an identity with a list of supported attributes it may generate */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attributes {
    String[] value() default {};

    Class<? extends Identity>[] identities() default {};
}

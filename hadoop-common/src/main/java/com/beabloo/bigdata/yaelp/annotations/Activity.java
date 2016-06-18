package com.beabloo.bigdata.yaelp.annotations;

import com.beabloo.bigdata.yaelp.spec.Environment;
import com.beabloo.bigdata.yaelp.spec.Trigger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Activity {

    Environment environment() default Environment.UNKNOWN;
    Trigger trigger() default Trigger.UNKNOWN;

}

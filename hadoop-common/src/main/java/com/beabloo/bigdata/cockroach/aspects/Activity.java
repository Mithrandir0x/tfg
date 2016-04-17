package com.beabloo.bigdata.cockroach.aspects;

import com.beabloo.bigdata.cockroach.spec.Event;
import com.beabloo.bigdata.cockroach.spec.Platform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Activity {

    Platform platform() default Platform.UNKNOWN;
    Event event() default Event.UNKNOWN;

}

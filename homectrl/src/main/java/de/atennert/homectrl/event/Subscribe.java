package de.atennert.homectrl.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods with this annotation will receive events with the
 * type of their parameter. Those methods must have only
 * <strong>one</strong> parameter!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe
{
    /**
     * The name of the field that contains the ID of device,
     * whose messages are supposed to be sent to the annotated
     * method. If not set, then all messages of a given type
     * will be forwarded.
     */
    String idSelector() default "";
}

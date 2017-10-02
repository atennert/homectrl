package de.atennert.homectrl.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields with this annotation will have the default
 * value of a given device injected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DeviceValue
{
    /**
     * The name of the field that contains the ID of device,
     * whose default value is supposed to be injected into
     * the annotated field.
     */
    String idSelector();
}

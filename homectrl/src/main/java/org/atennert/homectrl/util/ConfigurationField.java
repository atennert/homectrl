package org.atennert.homectrl.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to link a constructor parameter of controls or processors
 * with a field in the device description file. Every processor
 * or control constructor parameter must use this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ConfigurationField
{
    /**
     * @return the name of the field in the device description
     */
    String fieldId();
}

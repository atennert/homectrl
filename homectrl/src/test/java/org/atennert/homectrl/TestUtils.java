
package org.atennert.homectrl;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;

public class TestUtils
{
    @Deprecated
    protected void setSuperSuperField(Object owner, String fieldName, Object value)
    {
        try
        {
            Field f = owner.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(owner, value);
        }
        catch ( final Exception e )
        {
            fail("Unable to set " + fieldName + "! " + e.getMessage());
        }
    }

    @Deprecated
    protected void setSuperField(Object owner, String fieldName, Object value)
    {
        try
        {
            Field f = owner.getClass().getSuperclass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(owner, value);
        }
        catch ( final Exception e )
        {
            fail("Unable to set " + fieldName + "! " + e.getMessage());
        }
    }

    protected void setField(Object owner, String fieldName, Object value)
    {
        try
        {
            Field f = owner.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(owner, value);
        }
        catch ( final Exception e )
        {
            fail("Unable to set " + fieldName + "! " + e.getMessage());
        }
    }
}

package org.atennert.homectrl.controls.util;

/**
 * Since controllers are generated from a special device configuration
 * file, that is not directly accessible to Spring, this interface
 * identifies controllers that have an init method, that needs to be
 * called during the initialization by the ControlManager.
 */
public interface IInitializable
{
    public void init();
}

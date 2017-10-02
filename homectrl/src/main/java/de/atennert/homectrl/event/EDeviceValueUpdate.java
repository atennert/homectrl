package de.atennert.homectrl.event;

/**
 * Event object for updates of device values from actors and sensors.
 */
public class EDeviceValueUpdate
{
    public final int deviceId;

    public final Object value;

    public EDeviceValueUpdate(int deviceId, Object value)
    {
        this.deviceId = deviceId;
        this.value = value;
    }
}

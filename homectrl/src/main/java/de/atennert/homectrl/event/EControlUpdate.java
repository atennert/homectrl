package de.atennert.homectrl.event;

/**
 * Event object for control actions towards actors.
 */
public class EControlUpdate
{
    public final int actorId;

    public final Object value;

    public EControlUpdate(int actorId, Object value)
    {
        this.actorId = actorId;
        this.value = value;
    }
}

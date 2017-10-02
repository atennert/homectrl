package de.atennert.homectrl.controls;

import de.atennert.homectrl.event.EventBus;

public abstract class AbstractController<T>
{
    protected T actorValue;

    public int actorId;

    protected EventBus eventBus;

    public AbstractController(int actorId, T actorValue)
    {
        this.actorId = actorId;
        this.actorValue = actorValue;
    }

    public void setEventBus(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }
}

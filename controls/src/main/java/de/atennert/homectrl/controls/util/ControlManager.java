package de.atennert.homectrl.controls.util;

import java.util.Set;

import de.atennert.homectrl.controls.AbstractController;
import de.atennert.homectrl.event.EventBus;
import org.springframework.beans.factory.annotation.Autowired;

public class ControlManager
{
    private Set<AbstractController<?>> controls;

    private EventBus eventBus;

    public void setControls(Set<AbstractController<?>> controls)
    {
        this.controls = controls;
    }

    @Autowired
    public void setEventBus(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }



    public void init()
    {
        for (AbstractController<?> controller : controls)
        {
            controller.setEventBus(eventBus);

            if (controller instanceof IInitializable)
            {
                ((IInitializable)controller).init();
            }
            eventBus.register(controller);
        }
    }

    public void dispose()
    {
        controls = null;
        eventBus = null;
    }
}

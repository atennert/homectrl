package de.atennert.homectrl.dataprocessing.util;

import java.util.Set;

import de.atennert.homectrl.dataprocessing.AbstractDataProcessor;
import de.atennert.homectrl.event.EventBus;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessorManager
{
    private Set<AbstractDataProcessor<?>> processors;

    private EventBus eventBus;

    public void setProcessors(Set<AbstractDataProcessor<?>> processors)
    {
        this.processors = processors;
    }

    @Autowired
    public void setEventBus(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }



    public void init()
    {
        for (AbstractDataProcessor<?> processor : processors)
        {
            processor.setEventBus(eventBus);
            eventBus.register(processor);
        }
    }

    public void dispose()
    {
        processors = null;
        eventBus = null;
    }
}

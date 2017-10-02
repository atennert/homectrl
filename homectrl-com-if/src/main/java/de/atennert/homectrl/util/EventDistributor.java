package de.atennert.homectrl.util;

import de.atennert.homectrl.communication.CommunicationHandler;
import de.atennert.homectrl.registration.DataDescription;

import java.util.List;

/**
 * This class will get updates from the {@link CommunicationHandler} about
 * incoming events and outgoing control actions.
 */
public class EventDistributor implements EventObserver
{
    private final List<EventObserver> observers;

    public EventDistributor( final List<EventObserver> observers ) {this.observers = observers;}

    @Override
    public void notify( final DataDescription description, final Object value )
    {
        observers.forEach( o -> o.notify( description, value ) );
    }
}

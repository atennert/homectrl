package de.atennert.homectrl.util;

import de.atennert.homectrl.registration.DataDescription;

/**
 * This interface is implemented by classes, which receive observed events from the
 * {@link EventDistributor}.
 */
public interface EventObserver
{
    EventObserver STUB = ( description, value ) -> {};

    void notify( DataDescription description, Object value );
}

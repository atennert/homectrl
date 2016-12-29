/*******************************************************************************
 * Copyright 2016 Andreas Tennert
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
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

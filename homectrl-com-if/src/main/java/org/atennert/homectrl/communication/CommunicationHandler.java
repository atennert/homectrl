/*******************************************************************************
 * Copyright 2014 Andreas Tennert
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

package org.atennert.homectrl.communication;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.atennert.com.communication.ICommunicatorAccess;
import org.atennert.com.communication.IDataAcceptance;
import org.atennert.com.util.DataContainer;
import org.atennert.com.util.MapDataContainer;
import org.atennert.homectrl.DataType;
import org.atennert.homectrl.event.EControlUpdate;
import org.atennert.homectrl.event.EDeviceValueUpdate;
import org.atennert.homectrl.event.EventBus;
import org.atennert.homectrl.event.Subscribe;
import org.atennert.homectrl.registration.DataDescription;
import org.atennert.homectrl.registration.IHostAddressBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import rx.Single;
import rx.SingleSubscriber;

/**
 * This component is the binding between HomeCtrl and COM-framework.
 */
public class CommunicationHandler implements IDataAcceptance
{
    private final Logger log = LoggerFactory.getLogger( this.getClass() );

    private ICommunicatorAccess communicator;

    private IHostAddressBook hostAddressBook;

    private EventBus eventBus;

    @Autowired
    public void setCommunicator( ICommunicatorAccess communicator )
    {
        this.communicator = communicator;
    }

    @Autowired
    public void setEventBus( EventBus eventBus )
    {
        this.eventBus = eventBus;
    }

    @Required
    public void setHostLibrary( IHostAddressBook hostAddressBook )
    {
        this.hostAddressBook = hostAddressBook;
    }

    @Override
    public void accept(String senderAddress, DataContainer data) {
        evaluateData(senderAddress, data)
                .subscribe(data.subscriber);
    }

    private Single<DataContainer> evaluateData( String senderAddress, DataContainer data )
    {
        Map<String, Object> valueMap;
        if( data instanceof MapDataContainer )
        {
            valueMap = ((MapDataContainer) data).getData();
        }
        else
        {
            valueMap = new HashMap<>();
            valueMap.put( data.dataId, data.data );
        }
        Set<DataDescription> deviceDescriptions = hostAddressBook.getHostDevices( senderAddress,
                valueMap.keySet() );

        for( DataDescription description : deviceDescriptions )
        {
            // we know, that the value is a String since it comes from the
            // interpreter
            eventBus.post(
                    description.id,
                    new EDeviceValueUpdate( description.id,
                            DataType.getTypeValue( description.dataType,
                                    (String) valueMap.get( description.referenceId ) ) ) );
        }
        return Single.just(null);
    }

    @Subscribe
    public void control( EControlUpdate event )
    {
        log.info( "[control] " + event.actorId + " := " + event.value );
        DataDescription entry = hostAddressBook.getHostInformation( event.actorId );

        if( entry == null )
        {
            log.error( "[control] tried to control unregistered device: " + event.actorId );
        }
        else
        {
            communicator.send( entry.hostName, new DataContainer( entry.referenceId, event.value, resultSubscriber ) );
        }
    }

    private final SingleSubscriber<DataContainer> resultSubscriber = new SingleSubscriber<DataContainer>() {
        @Override
        public void onSuccess(DataContainer container) {
            if (container != null) {
                log.debug("[resultSubscriber.onSuccess] " + container.dataId + " := " + container.data);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            log.warn( "[resultSubscriber.onError] " + throwable.getMessage() );
        }
    };
}

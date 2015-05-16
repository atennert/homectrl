package org.atennert.homectrl.communication;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.atennert.com.communication.ICommunicatorAccess;
import org.atennert.com.util.DataContainer;
import org.atennert.homectrl.event.EControlUpdate;
import org.atennert.homectrl.event.EventBus;
import org.atennert.homectrl.registration.IHostAddressBook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class CommunicationHandlerTest
{
    private CommunicationHandler handler;

    @Mock
    private EventBus eventBus;
    @Mock
    private ICommunicatorAccess communicator;
    @Mock
    private IHostAddressBook addressBook;

    @Before
    public void setUp()
    {
        initMocks( this );

        handler = new CommunicationHandler();

        handler.setEventBus( eventBus );
        handler.setCommunicator( communicator );
        handler.setHostLibrary( addressBook );
    }

    @Test
    public void dontSendCommandsForUnknownActors()
    {
        EControlUpdate event = new EControlUpdate( 1, 3 );

        when( addressBook.getHostInformation( event.actorId ) ).thenReturn( null );

        handler.control( event );

        verify( communicator, never() ).send( anyString(), any( DataContainer.class ) );
    }

    public void sendCommandForActorUpdate()
    {
        // TODO implement test
    }

    // TODO implement Tests for receiving updates
}

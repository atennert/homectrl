package de.atennert.homectrl.communication;

import de.atennert.com.communication.ICommunicatorAccess;
import de.atennert.com.util.DataContainer;
import de.atennert.homectrl.DataType;
import de.atennert.homectrl.event.EControlUpdate;
import de.atennert.homectrl.event.EventBus;
import de.atennert.homectrl.registration.DataDescription;
import de.atennert.homectrl.registration.IHostAddressBook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import rx.SingleSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommunicationHandlerTest
{
    private static final String ADDRESS = "address";

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
        final EControlUpdate event = new EControlUpdate( 1, 3 );
        when( addressBook.getHostInformation( event.actorId ) ).thenReturn( null );

        handler.control( event );

        verify( communicator, never() ).send( anyString(), any( DataContainer.class ) );
    }

    @Test
    public void sendCommandForActorUpdate()
    {
        final EControlUpdate event = new EControlUpdate( 1, 3 );
        final DataDescription dataDesc = new DataDescription( 1, DataType.INTEGER, "host", "3", 1 );
        when( addressBook.getHostInformation( event.actorId ) ).thenReturn( dataDesc );

        handler.control( event );

        verify( communicator ).send( eq( dataDesc.hostName ),
                argThat( new ArgumentMatcher<DataContainer>()
                {
                    @Override
                    public boolean matches( Object data )
                    {
                        return ((DataContainer) data).dataId.equals( dataDesc.referenceId )
                                && ((DataContainer) data).data.equals( event.value );
                    }
                } ) );
    }

    @Test
    public void acceptSimpleData()
    {
        final SingleSubscriber<DataContainer> subscriber = spy(new SingleSubscriber<DataContainer>() {
            @Override
            public void onSuccess(DataContainer dataContainer) {}
            @Override
            public void onError(Throwable throwable) {}
        });
        final DataContainer container = new DataContainer("id", "data", subscriber);

        handler.accept(ADDRESS, container);

        verify(subscriber).onSuccess(isNull(DataContainer.class));
    }
}

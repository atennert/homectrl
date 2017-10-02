package de.atennert.homectrl.interpretation;

import de.atennert.com.communication.IDataAcceptance;
import de.atennert.com.interpretation.IInterpreter;
import de.atennert.com.util.DataContainer;
import de.atennert.com.util.MapDataContainer;
import de.atennert.com.util.MessageContainer;
import de.atennert.com.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Interpreter for parameter messages (key-value lists).
 */
public class ParameterInterpreter implements IInterpreter
{

    private static Logger log = LoggerFactory.getLogger( ParameterInterpreter.class );

    public DataContainer decode( MessageContainer message )
    {
        Map<String, Object> map = translateMessage( message.message );

        if( map.size() < 1 || message.hasException())
        {
            log.warn( "interpreting: " + message.error );
            return null;
        }
        else
        {
            return new MapDataContainer( map, null );
        }
    }

    public String encode( DataContainer data )
    {
        if( data instanceof MapDataContainer )
        {
            @SuppressWarnings( "unchecked" )
            Map<String, Object> dataMap = (Map<String, Object>) data.data;
            StringBuilder result = new StringBuilder();

            for( Entry<String, Object> entry : dataMap.entrySet() )
            {
                result.append( "&" ).append( entry.getKey() ).append( "=" ).append( formatValue( entry.getValue() ) );
            }

            return result.toString().replaceFirst( "&", "" );
        }
        else if( data != null && data.dataId != null && !data.dataId.isEmpty() )
        {
            return data.dataId + "=" + formatValue( data.data );
        }
        return null;
    }

    @Override
    public void interpret(MessageContainer messageContainer, final Session session, IDataAcceptance acceptance, Scheduler scheduler) {
        log.trace( "interpreting: " + messageContainer.message );

        if (messageContainer.hasException() && !MessageContainer.Exception.EMPTY.equals( messageContainer.error )){
            session.call(null);
            return;
        }

        Map<String, Object> map = translateMessage( messageContainer.message );

        acceptance.accept( session.getSender(), new MapDataContainer(map,
                new SingleSubscriber<DataContainer>() {
                    @Override
                    public void onSuccess(DataContainer dataContainer) {
                        Single.just(dataContainer)
                                .subscribeOn(session.scheduler)
                                .map(data -> encode(data))
                                .subscribe(session);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("[ParameterInterpreter.Subscriber.accept] ERROR");
                        Single.<String>just(null)
                                .subscribeOn(session.scheduler)
                                .subscribe(session);
                    }
                }) );
    }

    protected Map<String, Object> translateMessage( String message )
    {
        Map<String, Object> map = new HashMap<>();
        if( message != null )
        {
            String[] params = message.split( "&" );
            for( String param : params )
            {
                String[] keyVal = param.split( "=" );
                if( keyVal.length == 2 && !keyVal[0].equals( "" ) )
                {
                    map.put( keyVal[0], keyVal[1] );
                }
                else if( keyVal.length == 1 && !keyVal[0].equals( "" ) )
                // Bug fix to read empty values
                {
                    map.put( keyVal[0], null );
                }
            }
        }

        return map;
    }

    protected String formatValue( Object value )
    {
        return value.toString();
    }
}

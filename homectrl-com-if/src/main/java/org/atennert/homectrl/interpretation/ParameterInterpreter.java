/*******************************************************************************
 * Copyright 2012 Andreas Tennert
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

package org.atennert.homectrl.interpretation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.atennert.com.communication.IDataAcceptance;
import org.atennert.com.interpretation.IInterpreter;
import org.atennert.com.registration.INodeRegistration;
import org.atennert.com.util.DataContainer;
import org.atennert.com.util.MapDataContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interpreter for parameter messages (key-value lists).
 */
public class ParameterInterpreter implements IInterpreter
{

    private static Logger log = LoggerFactory.getLogger( ParameterInterpreter.class );

    public DataContainer decode( String message )
    {
        Map<String, Object> map = translateMessage( message );

        if( map.size() < 1 )
        {
            return null;
        }
        else
        {
            return new MapDataContainer( map );
        }
    }

    public String encode( DataContainer data )
    {
        if( data instanceof MapDataContainer )
        {
            @SuppressWarnings( "unchecked" )
            Map<String, Object> dataMap = (Map<String, Object>) data.data;
            String result = "";

            for( Entry<String, Object> entry : dataMap.entrySet() )
            {
                result += "&" + entry.getKey() + "=" + formatValue( entry.getValue() );
            }

            return result.replaceFirst( "&", "" );
        }
        else if( data.dataId != null && !data.dataId.isEmpty() )
        {
            return data.dataId + "=" + formatValue( data.data );
        }
        return null;
    }

    public String interpret( String message, String sender, IDataAcceptance acceptance,
            INodeRegistration nr )
    {
        log.trace( "interpreting: " + message );

        Map<String, Object> map = translateMessage( message );

        DataContainer status;
        try
        {
            status = acceptance.evaluateData( sender, new MapDataContainer( map ) ).get();
        }
        catch( Exception e )
        {
            log.error( e.getMessage() );
            return "";
        }

        return encode( status );
    }

    private Map<String, Object> translateMessage( String message )
    {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] params = message.split( "&" );
        for( String param : params )
        {
            String[] keyVal = param.split( "=" );
            if( keyVal.length == 2 && !keyVal[0].equals( "" ) )
            {
                map.put( keyVal[0], keyVal[1] );
            }
            else if( keyVal.length == 1 && !keyVal[0].equals( "" ) )
            // Bugfix to read empty values
            {
                map.put( keyVal[0], null );
            }
        }

        return map;
    }

    protected String formatValue( Object value )
    {
        return value.toString();
    }
}

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

package de.atennert.homectrl.communication;

import java.io.BufferedReader;
import java.io.IOException;

import de.atennert.com.util.MessageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for HttpReader and HttpSender
 */
public class HttpHelper
{

    private static final Logger log = LoggerFactory.getLogger( HttpHelper.class );

    /**
     * Reads the input from a given BufferedReader. The input is supposed to be
     * HTTP code containing "content-type", "content-lenght" and the content
     * itself.
     *
     * @param input
     * @return content-type + "@@" + content or null if at least one of them was
     *         not found.
     */
    protected static MessageContainer getContent( BufferedReader input )
    {
        boolean content = false;
        String buffer, content_type = null, message = "";
        long content_length = -1;

        MessageContainer result = new MessageContainer(MessageContainer.Exception.IO);

        // retrieve content info
        try
        // TODO fix the problem of messages without content-length
        {
            while( ((buffer = input.readLine()) != null) )
            {
                if( buffer.toLowerCase().startsWith( "content-type" ) )
                {
                    content = true;
                    content_type = buffer.substring( 14 );
                }
                if( buffer.toLowerCase().startsWith( "content-length" ) )
                {
                    content_length = Long.valueOf( buffer.substring( 16 ) );
                }
                message += buffer + "\n";
                if( message.endsWith( "\n\n" ) )
                {
                    break;
                }
            }

            log.trace( "receive message, content: " + content_type + ":" + content_length );

            // get the body (if there is one)
            if( content && (content_length > 0 || content_length == -1) )
            {
                message = "";
                int buf;
                // read content until end ...
                while( ((buf = input.read()) != -1) )
                {
                    message += (char) buf;
                    // ... or until known content length is reached
                    if( content_length != -1 && message.length() >= content_length )
                    {
                        break;
                    }
                }
                content_type = content_type.split( ";" )[0];
                result = new MessageContainer( content_type.split( "/" )[1], message );
            }
            else if( content )
            { // Bugfix to initiate status return for empty messages
                result = new MessageContainer( content_type.split( ";" )[0].split( "/" )[1], null, MessageContainer.Exception.EMPTY );
            }
            else
            {
                result = new MessageContainer(MessageContainer.Exception.EMPTY);
            }
        }
        catch( NumberFormatException e )
        {
            log.error( e.getMessage() );
        }
        catch( IOException e )
        {
            log.error( e.getMessage() );
        }

        return result;
    }
}

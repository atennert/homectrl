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

package org.atennert.homectrl.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.atennert.com.communication.ISender;
import org.atennert.com.util.MessageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class sends HTTP messages.
 */
public class HttpSender implements ISender
{

    private static final Logger log = LoggerFactory.getLogger( HttpSender.class );

    private String post_header = "POST / HTTP/1.1\r\n" + "Content-type: text/CONTENT_TYPE\r\n"
            + "Content-length: ";
    private final String get_header = "GET / HTTP/1.1\r\n\r\n";

    public MessageContainer send( String address, MessageContainer message )
    {
        log.info( "sending " + message + " to " + address );
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        // seperate address parts
        String addressParts[] = address.split( ":" );
        String host = addressParts[0];
        int port = Integer.valueOf( addressParts[1] );

        try
        {
            // open connection
            socket = new Socket( host, port );
            out = new PrintWriter( socket.getOutputStream(), true );
            in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        }
        catch( UnknownHostException e )
        {
            log.error( "Don't know about host: " + host + "." );
            return null;
        }
        catch( IOException e )
        {
            log.error( "Couldn't get I/O for the connection to: " + host + "." );
            return null;
        }

        MessageContainer response = null;

        try
        {
            if( message != null )
            { // make a POST request
                out.print( post_header.replace( "CONTENT_TYPE", message.interpreter )
                        + message.message.length() + "\r\n\r\n" + message.message );
            }
            else
            { // make a GET request
                out.print( get_header );
            }
            out.flush();

            // check for and receive response
            if( in.readLine().matches( "(HTTP/\\d{1}\\.\\d{1})(\\s)(200)(\\s)(OK)" ) )
            {
                response = HttpHelper.getContent( in );
            }
            else
            {
                response = null;
            }

            // close connection
            out.close();
            in.close();
            socket.close();
        }
        catch( IOException e )
        {
            log.error( "I/O for the connection to " + host + " was lost during receiving." );
            return null;
        }

        return response;
    }

}

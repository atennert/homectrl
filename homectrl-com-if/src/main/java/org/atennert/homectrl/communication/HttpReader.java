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
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import org.atennert.com.interpretation.InterpreterManager;
import org.atennert.com.util.MessageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles HTTP requests.
 */
public class HttpReader implements Runnable
{

    private Socket clientSocket = null;
    private InterpreterManager im;

    private static final Logger logger = LoggerFactory.getLogger( HttpReader.class );

    /**
     * Constructor.
     *
     * @param communicator
     * @param clientSocket
     */
    public HttpReader( InterpreterManager im, Socket clientSocket)
    {
        this.clientSocket = clientSocket;
        this.im = im;
    }

    /**
     * This handles the request. It evaluates the message, extracts the content
     * and forwards it to the communicator adapter.
     */
    public void run()
    {
        try
        {
            BufferedReader input = new BufferedReader( new InputStreamReader(
                    clientSocket.getInputStream() ) );
            OutputStream output = clientSocket.getOutputStream();

            String clientAddress = clientSocket.getRemoteSocketAddress().toString();
            logger.info( "Got connection from: " + clientAddress );

            String message;
            String response = null;
            MessageContainer msgContainer;

            // check the header
            message = input.readLine() + "\n";
            // if (message.equals("POST / HTTP/1.1")){
            if( message.startsWith( "POST" ) && message.endsWith( "HTTP/1.1\n" ) )
            {

                msgContainer = HttpHelper.getContent( input );
                logger.info( "received message: " + msgContainer.message );

                if( message != null )
                {
                    // forward message to interpreter
                    try
                    {
                        response = im.interpret( msgContainer, (clientAddress.split( "/" )[1]) )
                                .get();
                    }
                    catch( InterruptedException e )
                    {
                        logger.error( "Interpreter was interupted." );
                    }
                    catch( ExecutionException e )
                    {
                        logger.error( "An error occured." );
                    }
                    catch( InstantiationException e )
                    {
                        logger.error( "An error occured." );
                    }
                    catch( IllegalAccessException e )
                    {
                        logger.error( "An error occured." );
                    }
                }

                // reply to sender
                if( response == null || response.equals( "" ) )
                {
                    output.write( "HTTP/1.1 200 OK\r\n\r\n".getBytes() );
                }
                else
                {
                    output.write( "HTTP/1.1 200 OK\r\n".getBytes() );
                    output.write( ("Content-type: text/" + msgContainer.interpreter + "\r\n")
                            .getBytes() );
                    output.write( ("Content-length: " + response.length() + "\r\n\r\n").getBytes() );
                    output.write( response.getBytes() );
                }
                // }else if (message.equals("GET / HTTP/1.1\n")){
            }
            else
            {
                output.write( "HTTP/1.1 501 Not Implemented\n\n".getBytes() );
            }

            output.flush();
            output.close();
            input.close();
            clientSocket.close();
        }
        catch( IOException e )
        {
            logger.error( "An I/O error occured." );
        }
    }
}

/*******************************************************************************
 * Copyright 2015 Andreas Tennert
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.atennert.com.communication.AbstractReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import rx.Scheduler;

/**
 * Instances of this class wait at a certain port for incoming HTTP messages and
 * forward them to an abstract handle method.
 */
public abstract class AbstractHttpReceiver extends AbstractReceiver
{
    private final Logger log = LoggerFactory.getLogger( this.getClass() );

    private String serverAddress;
    private int serverPort;
    private ServerSocket serverSocket;

    @Required
    public void setAddress( String address )
    {
        String[] addressParts = address.split( ":" );
        this.serverAddress = addressParts[0];
        this.serverPort = Integer.parseInt( addressParts[1] );
    }

    public String getAddress()
    {
        return serverAddress + ":" + serverPort;
    }

    @Override
    public void run()
    {
        openServerSocket();
        while( !isInterrupted() )
        {
            Socket clientSocket = null;
            try
            {
                clientSocket = this.serverSocket.accept();
            }
            catch( IOException e )
            {
                if( isInterrupted() )
                {
                    log.info( "[run] Server Stopped." );
                    return;
                }
                throw new RuntimeException( "[run] Error accepting client connection", e );
            }
            handleReceive( clientSocket );
        }
        log.info( "[run] HTTP server stopped." );
    }

    protected abstract void handleReceive( Socket clientSocket );

    /**
     * Open the socket for receiving messages.
     */
    private void openServerSocket()
    {
        try
        {
            this.serverSocket = new ServerSocket( this.serverPort );
            log.info( "[openServerSocket] HTTP server started on port: " + this.serverPort );
        }
        catch( IOException e )
        {
            throw new RuntimeException( "[openServerSocket] Cannot open port " + this.serverPort, e );
        }
    }
}

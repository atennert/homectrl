package de.atennert.homectrl.communication;

import de.atennert.com.communication.AbstractReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Instances of this class wait at a certain port for incoming HTTP messages and
 * forward them to an abstract handle method.
 */
abstract class AbstractHttpReceiver extends AbstractReceiver
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
            Socket clientSocket;
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

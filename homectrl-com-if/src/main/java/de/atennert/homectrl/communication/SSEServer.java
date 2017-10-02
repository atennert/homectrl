package de.atennert.homectrl.communication;

import java.net.Socket;

import de.atennert.com.communication.ISender;
import de.atennert.com.util.MessageContainer;

/**
 * Server class for Server-Sent Events. This class is a receiver as well as a
 * server. <br>
 * <br>
 * The receiver is used so clients can open a connection to this server. This
 * connection is not closed by the server except for application shutdown, since
 * it is used for sending the event messages. This connection cannot be used to
 * receive commands! Use other receivers for this purpose! <br>
 * <br>
 * The server implementation sends update events to all registered clients.
 */
public class SSEServer extends AbstractHttpReceiver implements ISender
{
    public MessageContainer send( String address, MessageContainer message )
    {
        // TODO use some local address or other unique identifier as address for
        // sending SSEs
        // TODO forward events to all clients
        return null;
    }

    @Override
    protected void handleReceive( Socket clientSocket )
    {
        // TODO register client for SSEs
        // TODO send complete status
    }

}

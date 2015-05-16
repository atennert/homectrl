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

import java.net.Socket;

import org.atennert.com.communication.ISender;
import org.atennert.com.util.MessageContainer;

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

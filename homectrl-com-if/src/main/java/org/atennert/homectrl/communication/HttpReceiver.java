package org.atennert.homectrl.communication;

import java.net.Socket;

public class HttpReceiver extends AbstractHttpReceiver
{
    @Override
    protected void handleReceive( Socket clientSocket )
    {
        scheduler.createWorker().schedule(() ->
                new Thread( new HttpReader( interpreter, clientSocket, scheduler ) ).start());
    }
}

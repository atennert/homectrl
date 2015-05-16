package org.atennert.homectrl.communication;

import java.net.Socket;

public class HttpReceiver extends AbstractHttpReceiver
{

    @Override
    protected void handleReceive( Socket clientSocket )
    {
        new Thread( new HttpReader( interpreter, clientSocket ) ).start();
    }

}

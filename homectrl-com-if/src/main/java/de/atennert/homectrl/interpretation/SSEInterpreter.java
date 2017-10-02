package de.atennert.homectrl.interpretation;

import de.atennert.com.communication.IDataAcceptance;
import de.atennert.com.interpretation.IInterpreter;
import de.atennert.com.util.DataContainer;
import de.atennert.com.util.MessageContainer;
import de.atennert.com.util.Session;
import rx.Scheduler;

/**
 * Interpreter for the specific use with the SSEServer class. <br>
 * <br>
 * TODO description
 */
public class SSEInterpreter implements IInterpreter
{
    @Override
    public DataContainer decode( MessageContainer message )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String encode( DataContainer data )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void interpret(MessageContainer messageContainer, Session session, IDataAcceptance iDataAcceptance, Scheduler scheduler) {
    }
}

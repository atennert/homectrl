package org.atennert.homectrl.registration;

import java.util.HashSet;
import java.util.Set;

import org.atennert.homectrl.util.IAddress;

public class NodeDescription
{
    final String name;

    private final Set<IAddress> sendAddresses = new HashSet<IAddress>();

    private final Set<IAddress> receiveAddresses = new HashSet<IAddress>();

    private final Set<String> interpreters = new HashSet<String>();

    public NodeDescription( String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj != null && obj instanceof NodeDescription
                && name.equals( ((NodeDescription) obj).name ) )
        {
            return true;
        }
        return false;
    }

    public void addSendAddress( IAddress address )
    {
        sendAddresses.add( address );
    }

    public Set<IAddress> getSendAddresses()
    {
        return sendAddresses;
    }

    public String getSendProtocol( String address )
    {
        for (IAddress addressObj : sendAddresses)
        {
            if (addressObj.getAddress().equals( address ))
            {
                return addressObj.getProtocol();
            }
        }
        return null;
    }

    public void addReceiveAddress( IAddress address )
    {
        receiveAddresses.add( address );
    }

    public Set<IAddress> getReceiveAddresses()
    {
        return receiveAddresses;
    }

    public String getReceiveProtocol( String address )
    {
        for (IAddress addressObj : receiveAddresses)
        {
            if (addressObj.getAddress().equals( address ))
            {
                return addressObj.getProtocol();
            }
        }
        return null;
    }

    public void addInterpreter( String interpreter )
    {
        interpreters.add( interpreter );
    }

    public Set<String> getInterpreters()
    {
        return new HashSet<String>( interpreters );
    }
}

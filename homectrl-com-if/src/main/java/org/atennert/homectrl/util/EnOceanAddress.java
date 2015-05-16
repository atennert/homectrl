package org.atennert.homectrl.util;

public class EnOceanAddress implements IAddress
{
    private final String protocol;

    // TODO use read MAC address from enocean transceiver and use it for something
    private final String enOceanAddress;
    private final String enOceanPort;


    EnOceanAddress( String protocol, String enOceanAddress, String enOceanPort)
    {
        this.protocol = protocol;
        this.enOceanAddress = enOceanAddress;
        this.enOceanPort = enOceanPort;
    }


    public String getAddress()
    {
        return enOceanPort;
    }

    public String getProtocol()
    {
        return protocol;
    }

}

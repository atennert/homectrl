package de.atennert.homectrl.util;

public class HttpAddress implements IAddress
{
    private final String protocol;

    private final String httpAddress;
    private final String httpPort;


    HttpAddress( String protocol, String httpAddress, String httpPort)
    {
        this.protocol = protocol;
        this.httpAddress = httpAddress;
        this.httpPort = httpPort;
    }


    public String getAddress()
    {
        return httpAddress + ":" + httpPort;
    }

    public String getProtocol()
    {
        return protocol;
    }
}

package de.atennert.homectrl.util;

import java.util.Map;

public class AddressFactory
{
    // This cannot be instantiated
    private AddressFactory(){}

    public static IAddress getAddress(Map<String, String> addressData){
        String protocol = addressData.get( "protocol" );
        if ("http".equals( protocol ))
        {
            return new HttpAddress(
                    protocol,
                    addressData.get( "address" ),
                    addressData.get( "port" ));
        }
        else if ("enocean".equals( protocol ))
        {
            return new EnOceanAddress(
                    protocol,
                    addressData.get( "address" ),
                    addressData.get( "port" ));
        }
        else
        {
            return null;
        }
    }
}

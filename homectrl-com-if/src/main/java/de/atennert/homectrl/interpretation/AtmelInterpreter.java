package de.atennert.homectrl.interpretation;

import de.atennert.com.util.DataContainer;

/**
 * Interpreter for the ATMEL parameter protocol (home made protocol).
 */
public class AtmelInterpreter extends ParameterInterpreter
{
    @Override
    public String encode( DataContainer data )
    {
        if( data != null && data.dataId != null && !data.dataId.isEmpty() )
        {
            return "OUT=&" + super.encode( data ) + "&SUB=Senden";
        }
        return null;
    }

    @Override
    protected String formatValue( Object value )
    {
        if( value instanceof Boolean )
        {
            return (Boolean) value ? "1" : "0";
        }
        return super.formatValue( value );
    }
}

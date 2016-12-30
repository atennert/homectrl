/*******************************************************************************
 * Copyright 2012 Andreas Tennert
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

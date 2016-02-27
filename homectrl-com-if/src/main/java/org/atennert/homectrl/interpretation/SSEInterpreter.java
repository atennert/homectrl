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

package org.atennert.homectrl.interpretation;

import org.atennert.com.communication.IDataAcceptance;
import org.atennert.com.interpretation.IInterpreter;
import org.atennert.com.registration.INodeRegistration;
import org.atennert.com.util.DataContainer;
import org.atennert.com.util.MessageContainer;

/**
 * Interpreter for the specific use with the SSEServer class. <br>
 * <br>
 * TODO description
 */
public class SSEInterpreter implements IInterpreter
{

    public DataContainer decode( MessageContainer message )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String encode( DataContainer data )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String interpret( MessageContainer message, String sender, IDataAcceptance acceptance,
            INodeRegistration nr )
    {
        // TODO check client and register it for SSEs
        return null;
    }

}

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

package org.atennert.homectrl.registration;

import org.atennert.homectrl.DataType;

/**
 * Object that describes a data field or device in the HomeCtrl system.
 */
public class DataDescription
{
    /**
     * ID of the data field / controlled device in the HomeCtrl system. Server
     * wide unique ID!
     */
    public final int id;
    /** Type of the data */
    public final DataType dataType;

    /** Unique name of the host node */
    public final String hostName;

    /** Id of a data field on the host node */
    public final String referenceId;
    /** Bitmask of one is neccessary (use only with byte) */
    public final Byte bitMask;

    public final Object defaultValue;

    public DataDescription( int id,
            DataType dataType,
            String hostName,
            String referenceId,
            Object defaultValue,
            Byte bitMask)
    {
        this.id = id;
        this.dataType = dataType;
        this.hostName = hostName;
        this.referenceId = referenceId;
        this.defaultValue = defaultValue;
        this.bitMask = bitMask;
    }

    public DataDescription( int id,
            DataType dataType,
            String hostName,
            String referenceId,
            Object defaultValue)
    {
        this.id = id;
        this.dataType = dataType;
        this.hostName = hostName;
        this.referenceId = referenceId;
        this.defaultValue = defaultValue;
        this.bitMask = null;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj != null && obj instanceof DataDescription )
        {
            return id == ((DataDescription) obj).id;
        }
        return false;
    }
}

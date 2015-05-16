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

package org.atennert.homectrl.dataprocessing;

import java.util.HashMap;

import org.atennert.homectrl.event.EDeviceValueUpdate;
import org.atennert.homectrl.event.Subscribe;
import org.atennert.homectrl.util.ConfigurationField;

/**
 * Virtual sensor which returns the highest integer value from a set of sensors.
 */
public class HighIntegerProcessor extends AbstractDataProcessor<Integer>
{
    private HashMap<Integer, Integer> sensors;


    public HighIntegerProcessor(
            @ConfigurationField(fieldId="id") int id,
            @ConfigurationField(fieldId="value") Integer defaultValue,
            @ConfigurationField(fieldId="resources") HashMap<Integer, Integer> sensors)
    {
        super(id, defaultValue);
        this.sensors = sensors;
    }

    @Subscribe
    public void update(EDeviceValueUpdate event)
    {
        if ( sensors.containsKey(event.deviceId) )
        {
            sensors.put(event.deviceId, (Integer)event.value);

            processorValue = getHighestValue((Integer)event.value);
            eventBus.post(processorId, new EDeviceValueUpdate(processorId, processorValue));
        }
    }

    /**
     * @return the current highest sensor value
     */
    private int getHighestValue(final int startValue)
    {
        int highestValue = startValue;

        for ( Integer value : sensors.values() )
        {
            if ( value > highestValue )
            {
                highestValue = value;
            }
        }
        return highestValue;
    }
}

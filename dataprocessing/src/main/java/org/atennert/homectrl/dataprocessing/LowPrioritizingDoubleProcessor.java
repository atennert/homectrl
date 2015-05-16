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
 * Virtual sensor for temperature calculation that listens to other temperature sensors
 * and estimates a temperature with a given higher weight on the lowest temperature.
 * This can be useful to somehow eliminate influences like higher temperature values due
 * to sunshine or heat from building walls.
 *
 * @author Andreas Tennert
 */
public class LowPrioritizingDoubleProcessor extends AbstractDataProcessor<Double>
{
    private HashMap<Integer, Double> sensors;

    private int lowestValueWeight = 3;


    public LowPrioritizingDoubleProcessor(
            @ConfigurationField(fieldId="id") int id,
            @ConfigurationField(fieldId="value") double defaultValue,
            @ConfigurationField(fieldId="resources") HashMap<Integer, Double> sensors,
            @ConfigurationField(fieldId="lowestValueWeight") int lowestValueWeight)
    {
        super(id, defaultValue);
        this.sensors = sensors;
        // -1 because we already use the lowest temperature once in the general sum computation
        this.lowestValueWeight = lowestValueWeight - 1;
    }

    @Subscribe
    public void update(EDeviceValueUpdate event)
    {
        if ( sensors.containsKey(event.deviceId) )
        {
            sensors.put(event.deviceId, (Double)event.value);

            processorValue = getEstimatedValue((Double)event.value);
            eventBus.post(processorId, new EDeviceValueUpdate(processorId, processorValue));
        }
    }

    /**
     * Calculate the new estimated temperature using the following formula:
     *
     * (sum(values) + lowestValueWeight * lowestValue) / (count(values) + lowestValueWeight)
     *
     * @param lowestStartValue
     * @return
     */
    private double getEstimatedValue(final double lowestStartValue)
    {
        double valueSum = 0;
        double lowestValue = lowestStartValue;

        // calculate new value
        for ( Double value : sensors.values() )
        {
            if ( value < lowestValue )
            {
                lowestValue = value;
            }
            valueSum += value;
        }
        return ( valueSum + lowestValueWeight * lowestValue ) / ( sensors.size() + lowestValueWeight );
    }
}

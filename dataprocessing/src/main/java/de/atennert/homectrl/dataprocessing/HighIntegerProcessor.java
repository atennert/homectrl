package de.atennert.homectrl.dataprocessing;

import java.util.HashMap;

import de.atennert.homectrl.event.EDeviceValueUpdate;
import de.atennert.homectrl.event.Subscribe;
import de.atennert.homectrl.util.ConfigurationField;

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

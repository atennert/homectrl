package de.atennert.homectrl.dataprocessing;

import java.util.Collection;
import java.util.HashMap;

import de.atennert.homectrl.event.EDeviceValueUpdate;
import de.atennert.homectrl.event.Subscribe;
import de.atennert.homectrl.util.ConfigurationField;

/**
 * Virtual sensor which computes the logical AND from a set of boolean
 * sensor values.
 *
 * @author Andreas Tennert
 */
public class LogicalAndProcessor extends AbstractDataProcessor<Boolean>
{
    private HashMap<Integer, Boolean> sensors;


    public LogicalAndProcessor(
            @ConfigurationField(fieldId="id") int id,
            @ConfigurationField(fieldId="value") Boolean defaultValue,
            @ConfigurationField(fieldId="resources") HashMap<Integer, Boolean> sensors)
    {
        super(id, defaultValue);

        this.sensors = sensors;
    }

    @Subscribe
    public void update(EDeviceValueUpdate event)
    {
        if ( sensors.containsKey(event.deviceId) )
        {
            sensors.put(event.deviceId, (Boolean)event.value);

            processorValue = and(sensors.values());
            eventBus.post(processorId, new EDeviceValueUpdate(processorId, processorValue));
        }
    }

    public static boolean and(Collection<Boolean> booleans)
    {
        return !booleans.contains(Boolean.FALSE);
    }
}

package de.atennert.homectrl.dataprocessing;

import de.atennert.homectrl.event.EDeviceValueUpdate;
import de.atennert.homectrl.event.Subscribe;
import de.atennert.homectrl.util.ConfigurationField;

import java.util.Map;

/**
 * This sensor is used to combine sensor values with an or combinator.
 * It forwards <code>true</code> if at least one of the inputs is
 * <code>true</code>, <code>false</code> otherwise.
 */
public class LogicalOrProcessor extends AbstractDataProcessor<Boolean> {

    private final Map<Integer, Boolean> sensors;

    public LogicalOrProcessor(
            @ConfigurationField(fieldId = "id") int id,
            @ConfigurationField(fieldId = "resources") Map<Integer, Boolean> sensors)
    {
        super(id, false);

        this.sensors = sensors;
    }

    @Subscribe
    public void update(EDeviceValueUpdate event)
    {
        if (sensors.containsKey(event.deviceId))
        {
            sensors.put(event.deviceId, (Boolean)event.value);

            final boolean oldProcessorValue = processorValue;
            processorValue = sensors.values().stream().anyMatch(status -> status);

            if (oldProcessorValue != processorValue) {
                eventBus.post(processorId, new EDeviceValueUpdate(processorId, processorValue));
            }
        }
    }
}

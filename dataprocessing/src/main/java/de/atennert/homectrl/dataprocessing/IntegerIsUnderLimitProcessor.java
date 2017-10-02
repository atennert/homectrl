package de.atennert.homectrl.dataprocessing;

import java.util.Map;

import de.atennert.homectrl.event.EDeviceValueUpdate;
import de.atennert.homectrl.event.Subscribe;
import de.atennert.homectrl.util.ConfigurationField;

/**
 * Virtual sensor which checks if a sensor value is larger than a given limit.
 *
 * @author Andreas Tennert
 */
public class IntegerIsUnderLimitProcessor extends AbstractDataProcessor<Boolean>
{
    private final int limit;

    public int sourceSensorId;

    public IntegerIsUnderLimitProcessor(
            @ConfigurationField(fieldId="id") int id,
            @ConfigurationField(fieldId="value") Boolean defaultValue,
            @ConfigurationField(fieldId="resources") Map<Integer, Integer> sourceSensorIds,
            @ConfigurationField(fieldId="limit") int limit)
    {
        super(id, defaultValue);

        // there should be exactly one element in the map
        this.sourceSensorId = sourceSensorIds.keySet().iterator().next();
        this.limit = limit;
    }

    @Subscribe(idSelector = "sourceSensorId")
    public void update(EDeviceValueUpdate event)
    {
        processorValue = isValueUnderLimit((Integer)event.value, limit);
        eventBus.post(processorId, new EDeviceValueUpdate(processorId, processorValue));
    }

    private static Boolean isValueUnderLimit(Integer value, int limit)
    {
        return value < limit ? Boolean.TRUE : Boolean.FALSE;
    }
}

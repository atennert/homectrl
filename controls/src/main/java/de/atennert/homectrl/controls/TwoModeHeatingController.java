package de.atennert.homectrl.controls;

import de.atennert.homectrl.controls.util.IInitializable;
import de.atennert.homectrl.event.EControlUpdate;
import de.atennert.homectrl.event.EDeviceValueUpdate;
import de.atennert.homectrl.event.Subscribe;
import de.atennert.homectrl.util.ConfigurationField;
import de.atennert.homectrl.util.DeviceValue;

/**
 * Heating controller that uses an inside and an outside temperature sensor
 * and lower and upper bounds to control a boolean heating actor. It also
 * listens to an activation sensor, that controls whether the heating is
 * generally in use. It has to modes, each mode contains of a lower and
 * upper bound. The modes are controlled via a boolean sensor.
 */
public class TwoModeHeatingController extends AbstractController<Boolean> implements IInitializable
{
    public int activationSensorId;
    public int modeSensorId;
    public int outsideSensorId;
    public int insideSensorId;

    @DeviceValue(idSelector = "activationSensorId") private boolean activationSensorValue;

    @DeviceValue(idSelector = "modeSensorId") private boolean modeSensorValue;

    @DeviceValue(idSelector = "outsideSensorId") protected double outsideSensorValue;

    @DeviceValue(idSelector = "insideSensorId") protected double insideSensorValue;

    private boolean lastRequestedValue = false;

    /** Value (including higher) at which the heating is switched of */
    private final double upperLimit1;
    /** Value (including lower) at which the heating is switched on */
    private final double lowerLimit1;
    /** Value (including higher) at which the heating is switched of */
    private final double upperLimit2;
    /** Value (including lower) at which the heating is switched on */
    private final double lowerLimit2;



    public TwoModeHeatingController(
            @ConfigurationField(fieldId = "actorId") int actorId,
            @ConfigurationField(fieldId = "value") boolean value,
            @ConfigurationField(fieldId = "outsideSensorId") int outsideSensorId,
            @ConfigurationField(fieldId = "insideSensorId") int insideSensorId,
            @ConfigurationField(fieldId = "activationSensorId") int activationSensorId,
            @ConfigurationField(fieldId = "modeSensorId") int modeSensorId,
            @ConfigurationField(fieldId = "upperLimit1") double upperLimit1,
            @ConfigurationField(fieldId = "lowerLimit1") double lowerLimit1,
            @ConfigurationField(fieldId = "upperLimit2") double upperLimit2,
            @ConfigurationField(fieldId = "lowerLimit2") double lowerLimit2)
    {
        super(actorId, value);

        this.outsideSensorId = outsideSensorId;
        this.insideSensorId = insideSensorId;
        this.activationSensorId = activationSensorId;
        this.modeSensorId = modeSensorId;

        this.upperLimit1 = upperLimit1;
        this.lowerLimit1 = lowerLimit1;
        this.upperLimit2 = upperLimit2;
        this.lowerLimit2 = lowerLimit2;
    }



    public void init()
    {
        update();
    }

    @Subscribe(idSelector = "actorId")
    public void updateActorValue(EDeviceValueUpdate event)
    {
        actorValue = (Boolean)event.value;
    }

    @Subscribe(idSelector = "outsideSensorId")
    public void updateOutsideTemperature(EDeviceValueUpdate event)
    {
        outsideSensorValue = (Double)event.value;
        update();
    }

    @Subscribe(idSelector = "insideSensorId")
    public void updateInsideTemperature(EDeviceValueUpdate event)
    {
        insideSensorValue = (Double)event.value;
        update();
    }

    @Subscribe(idSelector = "activationSensorId")
    public void updateActivity(EDeviceValueUpdate event)
    {
        activationSensorValue = (Boolean)event.value;
        update();
    }

    @Subscribe(idSelector = "modeSensorId")
    public void updateMode(EDeviceValueUpdate event)
    {
        modeSensorValue = (Boolean)event.value;
        update();
    }

    private double getUpperLimit(){
        return modeSensorValue ? upperLimit2 : upperLimit1;
    }

    private double getLowerLimit(){
        return modeSensorValue ? lowerLimit2 : lowerLimit1;
    }



    private void update()
    {
        if ( activationSensorValue )
        {
            if ( lastRequestedValue )
            // heating is active
            {
                /*
                 * Outside is warmer then inside or it's to warm inside
                 * -> turn heating of
                 */
                lastRequestedValue = ! ( outsideSensorValue > insideSensorValue || insideSensorValue >= getUpperLimit() );
            }
            else
            // heating is inactive
            {
                /*
                 * Outside is colder then inside and it is to cold inside
                 * -> turn heating on
                 */
                lastRequestedValue = outsideSensorValue < getUpperLimit() && insideSensorValue < getLowerLimit();
            }
        }
        else
        {
            lastRequestedValue = false;
        }

        if ( lastRequestedValue != actorValue )
        {
            actorValue = lastRequestedValue;
            eventBus.post(new EControlUpdate(actorId, lastRequestedValue));
        }
    }
}

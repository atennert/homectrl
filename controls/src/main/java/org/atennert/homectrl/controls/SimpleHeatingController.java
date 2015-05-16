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

package org.atennert.homectrl.controls;

import org.atennert.homectrl.controls.util.IInitializable;
import org.atennert.homectrl.event.EControlUpdate;
import org.atennert.homectrl.event.EDeviceValueUpdate;
import org.atennert.homectrl.event.Subscribe;
import org.atennert.homectrl.util.ConfigurationField;
import org.atennert.homectrl.util.DeviceValue;

/**
 * Simple heating controller that uses an inside and an outside temperature sensor
 * and a lower and upper bound to control a boolean heating actor.
 *
 * @author Andreas Tennert
 */
public class SimpleHeatingController extends AbstractController<Boolean> implements IInitializable
{
    public int outsideSensorId;
    public int insideSensorId;

    @DeviceValue(idSelector = "outsideSensorId")
    protected double outsideSensorValue;

    @DeviceValue(idSelector = "insideSensorId")
    protected double insideSensorValue;

    private boolean lastRequestedValue = false;

    /** Value (including higher) at which the heating is switched of */
    private final double upperLimit;
    /** Value (including lower) at which the heating is switched on */
    private final double lowerLimit;



    public SimpleHeatingController(
            @ConfigurationField(fieldId="actorId") int actorId,
            @ConfigurationField(fieldId="value") boolean value,
            @ConfigurationField(fieldId="outsideSensorId") int outsideSensorId,
            @ConfigurationField(fieldId="insideSensorId") int insideSensorId,
            @ConfigurationField(fieldId="upperLimit") double upperLimit,
            @ConfigurationField(fieldId="lowerLimit") double lowerLimit)
    {
        super(actorId, value);

        this.outsideSensorId = outsideSensorId;
        this.insideSensorId = insideSensorId;

        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
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

    private void update()
    {
        if ( lastRequestedValue )
        // heating is active
        {
            /*
             * Outside is warmer then inside or it's to warm inside
             * -> turn heating of
             */
            lastRequestedValue = ! ( outsideSensorValue > insideSensorValue || insideSensorValue >= upperLimit );
        }
        else
        // heating is inactive
        {
            /*
             * Outside is colder then inside and it is to cold inside
             * -> turn heating on
             */
            lastRequestedValue = outsideSensorValue <= insideSensorValue && insideSensorValue < lowerLimit;
        }

        if ( lastRequestedValue != actorValue )
        {
            eventBus.post(new EControlUpdate(actorId, lastRequestedValue));
        }
    }
}

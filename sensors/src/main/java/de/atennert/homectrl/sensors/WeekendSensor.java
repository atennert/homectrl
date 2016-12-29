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

package de.atennert.homectrl.sensors;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.atennert.homectrl.event.EDeviceValueUpdate;
import de.atennert.homectrl.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A sensor implementation for the handling of double values.
 *
 * @author Andreas Tennert
 */
public class WeekendSensor
{
    private static Logger log = LoggerFactory.getLogger(WeekendSensor.class);

    private Thread updateThread = new Thread()
    {
        @Override
        public void run()
        {
            try
            {
                sleep(60000); // a little wait time at the beginning to not send updates during initialization.

                while ( true )
                {
                    Calendar cal = new GregorianCalendar();

                    final int startHour = 17;
                    final int stopHour = 23;

                    int currentDay = cal.get(Calendar.DAY_OF_WEEK);
                    int currentHour = cal.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = cal.get(Calendar.MINUTE);

                    //@formatter:off
                    value =
                        ( currentDay == Calendar.SATURDAY
                        || ( currentDay == Calendar.FRIDAY && currentHour >= startHour )
                        || ( currentDay == Calendar.SUNDAY && currentHour < stopHour ) )
                        ? Boolean.TRUE : Boolean.FALSE;
                    //@formatter:on

                    eventBus.post(id, new EDeviceValueUpdate(id, value));

                    int dayDifference = ( Calendar.SATURDAY - currentDay + ( value ? Calendar.SUNDAY : Calendar.FRIDAY ) ) % 7;
                    int hourDifference = ( ( value ? stopHour : startHour ) - currentHour ) % 24;
                    int minuteDifference = 60 - currentMinute + 1;

                    if ( hourDifference < 0 )
                    {
                        hourDifference += 24;
                        dayDifference--;
                    }

                    log.debug("[updateThread.run] sleeping for days: " + dayDifference + ", hours: " + hourDifference + ", minutes:" + minuteDifference + ", is weekend:" + value);
                    sleep(getSleepTime(dayDifference, hourDifference, minuteDifference));
                }
            }
            catch ( InterruptedException e )
            {
                log.info("[updateThread.run] was interupted.");
            }
        };
    };


    private final int id;
    private boolean value;

    private EventBus eventBus;


    public WeekendSensor(int id, Boolean value)
    {
        this.id = id;
        this.value = value;
    }



    @Autowired
    public void setEventBus(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }


    public void init()
    {
        updateThread.start();
    }

    public void dispose()
    {
        updateThread.interrupt();
    }


    private static long getSleepTime(long days, long hours, long minutes)
    {
        return ( ( ( ( days * 24 ) + hours ) * 60 ) + minutes ) * 60000;
    }
}

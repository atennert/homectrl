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

package org.atennert.homectrl.event;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.atennert.homectrl.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This event bus implementation allows for subscribers to register for
 * specific types of events or specific types of events from a certain
 * source.
 * <br><br>
 * Subscribers are registered via the register-method. Methods, that are
 * used for receiving events must be annotated with {@link Subscribe}.
 * Events are posted using the post-method.
 * <br><br>
 * This event bus uses a thread pool to execute the event distribution.
 */
public class EventBus
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ExecutorService threadPool;

    private Map<Class<?>, Set<Pair<Object, Method>>> subscribersForEventsFromType;
    private Map<Integer, Set<Pair<Object, Method>>> subscribersForEventsFromSource;


    /**
     * Initialize the event bus. This sets up the thread pool for distributing
     * the events and prepares the subscriber storage.
     */
    public void init()
    {
        // for now use only 1 thread to keep everything synchronized :-\
        threadPool = Executors.newSingleThreadExecutor();

        subscribersForEventsFromType = new HashMap<Class<?>, Set<Pair<Object, Method>>>();
        subscribersForEventsFromSource = new TreeMap<Integer, Set<Pair<Object, Method>>>();
    }

    /**
     * Clean up method. This shuts down the thread pool and clears the subscriber
     * storage.
     */
    public void dispose()
    {
        threadPool.shutdown();  // Disable new tasks from being submitted
        try
        {
            // Wait a while for existing tasks to terminate
            if ( !threadPool.awaitTermination(10, TimeUnit.SECONDS) )
            {
                threadPool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if ( !threadPool.awaitTermination(10, TimeUnit.SECONDS) )
                {
                    log.error("[UpdateMediator.deinit] Pool did not terminate");
                }
            }
        }
        catch ( InterruptedException ie )
        {
            // (Re-)Cancel if current thread also interrupted
            threadPool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

        subscribersForEventsFromType.clear();
        subscribersForEventsFromSource.clear();
    }


    /**
     * Register a new subscriber.
     * @param subscriber new subscriber
     */
    public void register(Object subscriber)
    {
        Method[] methods = subscriber.getClass().getMethods();
        for ( Method method : methods )
        {
            // check for subscribe annotation
            Subscribe annotation = method.getAnnotation(Subscribe.class);
            if ( annotation != null )
            {
                Pair<Object, Method> subscriberPair = new Pair<Object, Method>(subscriber, method);

                String idSelectorField = annotation.idSelector();
                if ( idSelectorField != null && !idSelectorField.isEmpty() )
                {
                    try
                    {
                        EventBus.<Integer> addToMapSet(subscribersForEventsFromSource, getField(subscriber, idSelectorField), subscriberPair);
                    }
                    catch ( Exception e )
                    {
                        log.error("Unable to read selector ID from subscriber: " + subscriber, e);
                    }
                }
                else
                {
                    EventBus.<Class<?>> addToMapSet(subscribersForEventsFromType, method.getParameterTypes()[0], subscriberPair);
                }
            }
        }
    }

    private Integer getField(Object owner, String fieldName) throws Exception
    {
        Field f = null;
        Class<?> clazz = owner.getClass();
        while(f == null){
            try{
                f = clazz.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e){
                clazz = owner.getClass().getSuperclass();
            }
        }
        f.setAccessible(true);
        return f.getInt(owner);
    }

    private static <T> void addToMapSet(Map<T, Set<Pair<Object, Method>>> map, T key, Pair<Object, Method> value)
    {
        Set<Pair<Object, Method>> values = map.get(key);
        if ( values == null )
        {
            map.put(key, new HashSet<Pair<Object, Method>>());
        }
        map.get(key).add(value);
    }

    /**
     * Post events on the event bus.
     * @param event the event to distribute to registered subscribers
     */
    public void post(Object event)
    {
        threadPool.execute(new EventDistributionTask(event));
    }

    public void post(int sourceId, Object event)
    {
        threadPool.execute(new EventDistributionTask(sourceId, event));
    }

    private void distributeEvent(Integer sourceId, Object event)
    {
        Set<Pair<Object, Method>> subscribers = new HashSet<Pair<Object, Method>> (subscribersForEventsFromType.get(event.getClass()));
        Collection<Pair<Object, Method>> sourceSubscribers;
        if ( sourceId != null && (sourceSubscribers = subscribersForEventsFromSource.get(sourceId)) != null)
        {
            subscribers.addAll(new HashSet<Pair<Object, Method>> (sourceSubscribers));
        }

        for ( Pair<Object, Method> subscriber : subscribers )
        {
            try
            {
                subscriber.second.invoke(subscriber.first, event);
            }
            catch ( IllegalAccessException e )
            {
                log.error("Unable to access method to submit event: " + event, e);
            }
            catch ( IllegalArgumentException e )
            {
                log.error("Method does not fit for event: " + event, e);
            }
            catch ( InvocationTargetException e )
            {
                log.error("Unable submit event: " + event, e);
            }
        }
    }

    private class EventDistributionTask implements Runnable
    {
        private final Object event;
        private final Integer sourceId;

        public EventDistributionTask(Object event)
        {
            this.event = event;
            sourceId = null;
        }

        public EventDistributionTask(int sourceId, Object event)
        {
            this.sourceId = sourceId;
            this.event = event;
        }

        @Override
        public void run()
        {
            distributeEvent(sourceId, event);
        }
    }
}

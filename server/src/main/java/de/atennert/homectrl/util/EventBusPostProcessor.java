/*
 * EventBusPostProcessor.java
 * Author: Patrick Meade
 * http://pmeade.blogspot.de/2012/06/using-guava-eventbus-with-spring.html
 *
 * EventBusPostProcessor.java is hereby placed into the public domain.
 * Use it as you see fit, and entirely at your own risk.
 */

package de.atennert.homectrl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.atennert.homectrl.event.Subscribe;
import de.atennert.homectrl.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * EventBusPostProcessor registers Spring beans with EventBus. All beans
 * containing methods with the @Subscribe annotation are registered.
 * @author Patrick Meade
 */
public class EventBusPostProcessor implements BeanPostProcessor
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventBus eventBus;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        // for each method in the bean
        Method[] methods = bean.getClass().getMethods();
        for ( Method method : methods )
        {
            // check the annotations on that method
            Annotation[] annotations = method.getAnnotations();
            for ( Annotation annotation : annotations )
            {
                // if it contains the Subscribe annotation
                if ( annotation.annotationType().equals(Subscribe.class) )
                {
                    // register it with the event bus
                    eventBus.register(bean);
                    log.trace("Bean {} containing method {} was subscribed to {}", new Object[] { beanName, method.getName(), EventBus.class.getCanonicalName() });
                    // we only need to register once
                    return bean;
                }
            }
        }

        return bean;
    }
}

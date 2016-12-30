
package de.atennert.homectrl.event;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import de.atennert.homectrl.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventBusTest extends TestUtils
{
    private EventBus eventBus;

    @Mock private ExecutorService threadPool;

    @Before
    public void setUp()
    {
        eventBus = new EventBus();
    }

    @Test
    public void postTest()
    {
        setField(eventBus, "threadPool", threadPool);

        eventBus.post(new Object());

        verify(threadPool).execute(any(Runnable.class));
    }

    @Test
    public void registrationAndPostSubscribedEvent() throws InterruptedException
    {
        TestSubscriberString subscriber = new TestSubscriberString();
        String event = "event";

        eventBus.init();

        eventBus.register(subscriber);

        eventBus.post(event);
        Thread.sleep(200);
        Assert.assertEquals(event, subscriber.event);
    }

    @Test
    public void registrationAndPostUnsubscribedEvent() throws InterruptedException
    {
        TestSubscriberInt subscriber = new TestSubscriberInt();
        String event = "event";

        eventBus.init();

        eventBus.register(subscriber);

        eventBus.post(event);
        Thread.sleep(200);
        Assert.assertNull(subscriber.event);
    }

    @Test
    public void multipleRegistrationAndPostSubscribedEvent() throws InterruptedException
    {
        TestSubscriberString subscriber1 = new TestSubscriberString();
        TestSubscriberString subscriber2 = new TestSubscriberString();
        TestSubscriberString subscriber3 = new TestSubscriberString();
        String event = "event";

        eventBus.init();

        eventBus.register(subscriber1);
        eventBus.register(subscriber2);
        eventBus.register(subscriber3);

        eventBus.post(event);
        Thread.sleep(200);
        Assert.assertEquals(event, subscriber1.event);
        Assert.assertEquals(event, subscriber2.event);
        Assert.assertEquals(event, subscriber3.event);
    }

    @Test
    public void multipleRegistrationAndPostSourceSubscribedEvent() throws InterruptedException
    {
        TestSubscriberInt subscriber1 = new TestSubscriberInt();
        TestSubscriberIntSrc subscriber2 = new TestSubscriberIntSrc();
        TestSubscriberString subscriber3 = new TestSubscriberString();

        subscriber2.id = 5;
        Integer event = 42;

        eventBus.init();

        eventBus.register(subscriber1);
        eventBus.register(subscriber2);
        eventBus.register(subscriber3);

        eventBus.post(subscriber2.id, event);
        Thread.sleep(300);
        Assert.assertEquals(event, subscriber1.event);
        Assert.assertEquals(event, subscriber2.event);
        Assert.assertNull(subscriber3.event);
    }

    @Test
    public void registrationAndPostMultipleEvent() throws InterruptedException
    {
        TestSubscriberInt subscriber = new TestSubscriberInt();
        String event1 = "event1";
        Integer event2 = 2;
        String event3 = "event3";
        Integer event4 = 4;

        eventBus.init();

        eventBus.register(subscriber);

        eventBus.post(event1);
        Thread.sleep(200);
        Assert.assertNull(subscriber.event);

        eventBus.post(event2);
        Thread.sleep(200);
        Assert.assertEquals(event2, subscriber.event);

        eventBus.post(event3);
        Thread.sleep(200);
        Assert.assertEquals(event2, subscriber.event);

        eventBus.post(event4);
        Thread.sleep(200);
        Assert.assertEquals(event4, subscriber.event);
    }

    private class TestSubscriberString
    {
        public Object event;

        @Subscribe
        public void getEvent(String event)
        {
            this.event = event;
        }
    }

    private class TestSubscriberInt
    {
        public Object event;

        @Subscribe
        public void getEvent(Integer event)
        {
            this.event = event;
        }
    }

    private class TestSubscriberIntSrc
    {
        public Object event;

        public int id;

        @Subscribe(idSelector = "id")
        public void getEvent(Integer event)
        {
            this.event = event;
        }
    }
}

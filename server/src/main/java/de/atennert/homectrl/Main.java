package de.atennert.homectrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main
{

    private static Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * @param args
     */
    public static void main(String[] args)
    {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/app.xml");
        context.registerShutdownHook();
        log.info("Server started.");

        try
        {
            while ( true )
            {
                Thread.sleep(5000);
            }
        }
        catch ( InterruptedException e )
        {
            log.debug(e.getMessage());
        }

        context.stop();
        context.close();
        log.info("Server stopped.");
    }
}

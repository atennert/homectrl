package de.atennert.homectrl.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import de.atennert.com.interpretation.InterpreterManager;
import de.atennert.com.util.MessageContainer;
import de.atennert.com.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Scheduler;
import rx.Single;

/**
 * This class handles HTTP requests.
 */
class HttpReader implements Runnable
{

    private final Socket clientSocket;
    private final InterpreterManager im;
    private final Scheduler scheduler;

    private static final Logger logger = LoggerFactory.getLogger( HttpReader.class );

    /**
     * Constructor.
     *
     * @param im The InterpreterManager
     * @param clientSocket The socket on which the connection is established
     */
    HttpReader(InterpreterManager im, Socket clientSocket, Scheduler scheduler)
    {
        this.clientSocket = clientSocket;
        this.im = im;
        this.scheduler = scheduler;
    }

    /**
     * This handles the request. It evaluates the message, extracts the content
     * and forwards it to the communicator adapter.
     */
    public void run()
    {
        try
        {
            final BufferedReader input = new BufferedReader( new InputStreamReader(
                    clientSocket.getInputStream() ) );
            final OutputStream output = clientSocket.getOutputStream();

            final String clientAddress = clientSocket.getRemoteSocketAddress().toString();
            logger.info( "Got connection from: " + clientAddress );

            // check the header
            String message = input.readLine() + "\n";
            // if (message.equals("POST / HTTP/1.1")){
            if( message.startsWith( "POST" ) && message.endsWith( "HTTP/1.1\n" ) )
            {

                final MessageContainer msgContainer = HttpHelper.getContent( input );
                logger.info( "received message: " + msgContainer.message );

                // forward message to interpreter
                Single.just(msgContainer)
                        .subscribe(im.interpret(new Session(scheduler) {
                            @Override
                            public String getSender() {
                                return clientAddress.split( "/" )[1];
                            }

                            @Override
                            public void call(String response) {
                                // reply to sender
                                try
                                {
                                    if (response == null || response.equals("")) {
                                        output.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                                    } else {
                                        output.write("HTTP/1.1 200 OK\r\n".getBytes());
                                        output.write(("Content-type: text/" + msgContainer.interpreter + "\r\n").getBytes());
                                        output.write(("Content-length: " + response.length() + "\r\n\r\n").getBytes());
                                        output.write(response.getBytes());
                                    }
                                    output.flush();
                                    output.close();
                                    input.close();
                                    clientSocket.close();
                                }
                                catch (IOException e)
                                {
                                    logger.error( "An I/O error occurred." );
                                }
                            }
                        }));
            // }else if (message.equals("GET / HTTP/1.1\n")){
            }
            else
            {
                output.write( "HTTP/1.1 501 Not Implemented\n\n".getBytes() );
                output.flush();
                output.close();
                input.close();
                clientSocket.close();
            }
        }
        catch( IOException e )
        {
            logger.error( "An I/O error occurred." );
        }
    }
}

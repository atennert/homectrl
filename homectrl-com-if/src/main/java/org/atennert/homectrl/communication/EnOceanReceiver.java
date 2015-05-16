/*******************************************************************************
 * Copyright 2014 Andreas Tennert
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

package org.atennert.homectrl.communication;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.atennert.com.communication.AbstractReceiver;
import org.atennert.com.util.MessageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Receiver for EnOcean messages.
 */
public class EnOceanReceiver extends AbstractReceiver
{

    private static final Logger log = LoggerFactory.getLogger( EnOceanReceiver.class );

    private static CommPortIdentifier serialPortId;
    @SuppressWarnings( "rawtypes" )
    private static Enumeration enumComm;
    private SerialPort serialPort;
    private InputStream inputStream;
    private Boolean serialPortOpened = false;
    private final int baudrate = 57600;
    private final int dataBits = SerialPort.DATABITS_8;
    private final int stopBits = SerialPort.STOPBITS_1;
    private final int parity = SerialPort.PARITY_NONE;
    private String portName = null;

    @Required
    public void setAddress( String address )
    {
        this.portName = address;
    }

    @Override
    public String getAddress()
    {
        return portName;
    }

    @Override
    public void run()
    {
        boolean stop = false;
        if( openSerialPort( portName ) != true )
        {
            log.warn( "EnOcean server stopped prematurely." );
            return;
        }

        while( !(stop || isInterrupted()) )
        {
            try
            {
                Thread.sleep( 1000 );
            }
            catch( final InterruptedException e )
            {
                stop = true;
            }
        }

        closeSerialPort();

        log.info( "EnOcean server stopped." );
    }

    /**
     * Initialize port connection
     *
     * @param portName
     * @return Serial port opened
     */
    boolean openSerialPort( String portName )
    {
        if( portName == null )
        {
            return false;
        }

        Boolean foundPort = false;
        if( serialPortOpened != false )
        {
            log.error( "Serialport already opened" );
            return false;
        }

        log.debug( "Open serial port" );
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while( enumComm.hasMoreElements() )
        {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if( portName.contentEquals( serialPortId.getName() ) )
            {
                foundPort = true;
                break;
            }
        }
        if( foundPort != true )
        {
            log.error( "Serial port not found: " + portName );
            return false;
        }
        try
        {
            serialPort = (SerialPort) serialPortId.open( "open and send", 100 );

            inputStream = serialPort.getInputStream();

            serialPort.addEventListener( new serialPortEventListener() );

            serialPort.notifyOnDataAvailable( true );

            serialPort.setSerialPortParams( baudrate, dataBits, stopBits, parity );

            log.info( "Serial port started." );
        }
        catch( final PortInUseException e )
        {
            log.error( "Port in use" );
        }
        catch( final IOException e )
        {
            log.error( "No access to InputStream" );
        }
        catch( final TooManyListenersException e )
        {
            log.error( "TooManyListenersException for serial port" );
        }
        catch( final UnsupportedCommOperationException e )
        {
            log.error( "Could not set interface parameter" );
        }

        serialPortOpened = true;
        return true;
    }

    /**
     * Close serial port.
     */
    void closeSerialPort()
    {
        if( serialPortOpened == true )
        {
            log.info( "Close serial port" );
            serialPort.close();
            serialPortOpened = false;
        }
        else
        {
            log.warn( "Serial port already closed" );
        }
    }

    /**
     * Read data from serial port and forward to interpreter.
     */
    void serialPortDataAvailable()
    {
        try
        {
            int num;
            final byte[] sync = new byte[1];
            byte[] header = new byte[5], data, optional;
            final byte[] checksumValue = new byte[1];

            while( inputStream.available() > 0 )
            {
                // get synchronization byte
                boolean syncByteFound = false;
                num = inputStream.read( sync, 0, sync.length );
                while( num > 0 && !syncByteFound )
                {
                    if( sync[0] == 0x55 )
                    {
                        syncByteFound = true;
                    }
                    else
                    {
                        num = inputStream.read( sync, 0, sync.length );
                    }
                }
                if( !syncByteFound )
                {
                    continue;
                }

                // read header {data length (2x), optional length, packet type,
                // checksum}
                header = new byte[5];
                num = inputStream.read( header, 0, header.length );
                if( num < header.length )
                {
                    continue;
                }

                // check header
                int checksum = 0;
                for( int i = 0; i < header.length - 1; i++ )
                {
                    checksum = CodingHelper.processCRC8( checksum,
                            CodingHelper.byteToInt( header[i] ) );
                }
                if( (checksum & 0xFF) != CodingHelper.byteToInt( header[4] ) )
                {
                    continue;
                }

                // get data
                data = new byte[(CodingHelper.byteToInt( header[0] ) << 2)
                        + CodingHelper.byteToInt( header[1] )];
                num = inputStream.read( data, 0, data.length );
                if( num < data.length )
                {
                    continue;
                }

                // get optional
                optional = new byte[CodingHelper.byteToInt( header[2] )];
                num = inputStream.read( optional, 0, optional.length );
                if( num < optional.length )
                {
                    continue;
                }

                // check payload
                num = inputStream.read( checksumValue, 0, checksumValue.length );
                if( num < checksumValue.length )
                {
                    continue;
                }

                // check payload checksum
                checksum = CodingHelper.calculatePayloadChecksum( data, optional );
                final boolean dataValid = (checksum & 0xFF) == CodingHelper
                        .byteToInt( checksumValue[0] );

                // create, encode and interpret message
                final byte[] message = new byte[3 + data.length + optional.length];
                message[0] = header[3]; // packet type
                message[1] = (byte) (dataValid ? 1 : 0);
                message[2] = header[2]; // length of optional
                System.arraycopy( optional, 0, message, 3, optional.length );
                System.arraycopy( data, 0, message, 3 + optional.length, data.length );

                interpreter
                        .interpret(
                                new MessageContainer( "enocean", new String( Base64Coder
                                        .encode( message ) ) ), null );
            }
        }
        catch( final IOException ex )
        {
            log.error( ex.getMessage() );
        }
        catch( InstantiationException e )
        {
            log.error( e.getMessage() );
        }
        catch( IllegalAccessException e )
        {
            log.error( e.getMessage() );
        }
    }

    /**
     * Event listener for serial port. Is called when new data is available.
     */
    class serialPortEventListener implements SerialPortEventListener
    {
        public void serialEvent( SerialPortEvent event )
        {
            switch( event.getEventType() )
            {
            case SerialPortEvent.DATA_AVAILABLE:
                serialPortDataAvailable();
                break;
            case SerialPortEvent.BI:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.FE:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            case SerialPortEvent.PE:
            case SerialPortEvent.RI:
            default:
            }
        }
    }
}

package org.atennert.homectrl.registration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DeviceRegistrationTest
{
    private static final String INTERPRETER_0 = "i0";
    private static final String INTERPRETER_1 = "i1";

    private static final String PROTOCOL_0 = "p0";
    private static final String PROTOCOL_1 = "p1";
    private static final String PROTOCOL_UNKOWN = "pu";

    private Map<String, Set<String>> interpreterProtocolRuleSet;

    private DeviceRegistration deviceRegistration;

    @Before
    public void setup()
    {
        interpreterProtocolRuleSet = new HashMap<String, Set<String>>();
        Set<String> interpreters = new HashSet<String>();
        interpreters.add( INTERPRETER_0 );
        interpreters.add( INTERPRETER_1 );
        interpreterProtocolRuleSet.put( PROTOCOL_0, interpreters );

        interpreters = new HashSet<String>();
        interpreters.add( INTERPRETER_1 );
        interpreterProtocolRuleSet.put( PROTOCOL_1, interpreters );

        deviceRegistration = new DeviceRegistration();
        deviceRegistration.setInterpreterProtocolRestrictions( interpreterProtocolRuleSet );
    }

    @Test
    public void testNoInterpreterForProtocol()
    {
        assertTrue( deviceRegistration.getInterpretersForProtocol( PROTOCOL_UNKOWN ).isEmpty() );
    }

    @Test
    public void testOneInterpreterForProtocol()
    {
        Set<String> interpreters = deviceRegistration.getInterpretersForProtocol( PROTOCOL_1 );

        assertEquals( 1, interpreters.size() );
        assertTrue( interpreters.contains( INTERPRETER_1 ) );
    }

    @Test
    public void testTwoInterpreterForProtocol()
    {
        Set<String> interpreters = deviceRegistration.getInterpretersForProtocol( PROTOCOL_0 );

        assertEquals( 2, interpreters.size() );
        assertTrue( interpreters.contains( INTERPRETER_0 ) );
        assertTrue( interpreters.contains( INTERPRETER_1 ) );
    }
}

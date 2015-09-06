package io.subutai.core.broker.api;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class BrokerExceptionTest
{
    private static final String MSG = "ERR";


    @Test
    public void testException() throws Exception
    {
        Exception cause = new Exception();

        BrokerException exception = new BrokerException( cause );

        assertEquals( cause, exception.getCause() );

        BrokerException exception2 = new BrokerException( MSG );

        assertEquals( MSG, exception2.getMessage() );
    }


    @Test
    public void testTopicEnum() throws Exception
    {
        assertTrue( Topic.valueOf( "HEARTBEAT_TOPIC" ) == Topic.HEARTBEAT_TOPIC );
    }
}
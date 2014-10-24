package org.safehaus.subutai.plugin.presto.impl;


import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.safehaus.subutai.common.protocol.AbstractOperationHandler;
import org.safehaus.subutai.common.tracker.ProductOperationState;
import org.safehaus.subutai.common.tracker.TrackerOperation;
import org.safehaus.subutai.plugin.common.mock.CommonMockBuilder;
import org.safehaus.subutai.plugin.presto.api.PrestoClusterConfig;
import org.safehaus.subutai.plugin.presto.impl.handler.AddWorkerNodeOperationHandler;
import org.safehaus.subutai.plugin.presto.impl.mock.PrestoImplMock;


public class AddWorkerEnvironmentContainerNodeOperationHandlerTest
{

    private PrestoImplMock mock;
    private AbstractOperationHandler handler;


    @Before
    public void setUp()
    {
        mock = new PrestoImplMock();
        handler = new AddWorkerNodeOperationHandler( mock, "test-cluster", "test-host" );
    }


    @Test
    public void testWithoutCluster()
    {
        handler.run();

        TrackerOperation po = handler.getTrackerOperation();
        Assert.assertTrue( po.getLog().toLowerCase().contains( "not exist" ) );
        Assert.assertEquals( po.getState(), ProductOperationState.FAILED );
    }


    @Test
    public void testWithUnconnectedAgents()
    {
        PrestoClusterConfig config = new PrestoClusterConfig();
        config.setClusterName( "test-cluster" );
        config.setWorkers( new HashSet<>( Arrays.asList( CommonMockBuilder.createAgent() ) ) );
        config.setCoordinatorNode( CommonMockBuilder.createAgent() );
        mock.setClusterConfig( config );

        handler.run();

        TrackerOperation po = handler.getTrackerOperation();
        Assert.assertTrue( po.getLog().toLowerCase().contains( "not connected" ) );
        Assert.assertEquals( po.getState(), ProductOperationState.FAILED );
    }
}

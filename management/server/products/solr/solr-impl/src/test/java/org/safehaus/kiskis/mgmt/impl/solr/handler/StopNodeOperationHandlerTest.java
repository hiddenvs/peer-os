package org.safehaus.kiskis.mgmt.impl.solr.handler;


import org.junit.Test;
import org.safehaus.kiskis.mgmt.api.solr.Config;
import org.safehaus.kiskis.mgmt.impl.solr.SolrImpl;
import org.safehaus.kiskis.mgmt.impl.solr.mock.SolrImplMock;
import org.safehaus.subutai.shared.operation.AbstractOperationHandler;
import org.safehaus.subutai.shared.operation.ProductOperationState;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class StopNodeOperationHandlerTest {


    @Test
    public void testWithoutCluster() {
        AbstractOperationHandler operationHandler = new StopNodeOperationHandler( new SolrImplMock(), "test-cluster", "test-lxc" );

        operationHandler.run();

        assertTrue( operationHandler.getProductOperation().getLog().contains( "not exist" ) );
        assertEquals( operationHandler.getProductOperation().getState(), ProductOperationState.FAILED );
    }


    @Test
    public void testFail() {
        SolrImpl solrImpl = new SolrImplMock().setClusterConfig( new Config() );
        AbstractOperationHandler operationHandler = new StopNodeOperationHandler( solrImpl, "test-cluster", "test-lxc" );

        operationHandler.run();

        assertTrue( operationHandler.getProductOperation().getLog().contains( "not connected" ) );
        assertEquals( operationHandler.getProductOperation().getState(), ProductOperationState.FAILED );
    }

}

package io.subutai.core.channel.impl.interceptor;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.cxf.message.Message;

import io.subutai.common.peer.LocalPeer;
import io.subutai.common.peer.Peer;
import io.subutai.common.peer.PeerInfo;
import io.subutai.common.settings.Common;
import io.subutai.core.channel.impl.util.InterceptorState;
import io.subutai.core.peer.api.PeerManager;
import io.subutai.core.security.api.SecurityManager;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


@RunWith( MockitoJUnitRunner.class )
public class ClientInInterceptorTest
{

    private static final String PEER_ID = "id";
    private static final int PORT = 80;
    private static final String ENV_ID = "env-id-123";
    @Mock
    SecurityManager securityManager;
    @Mock
    PeerManager peerManager;
    @Mock
    Peer peer;
    @Mock
    LocalPeer localPeer;
    @Mock
    PeerInfo peerInfo;

    ClientInInterceptor interceptor;


    @Before
    public void setUp() throws Exception
    {
        interceptor = spy( new ClientInInterceptor( securityManager, peerManager ) );
    }


    @Test
    public void testHandleMessage() throws Exception
    {
        Message message = InterceptorStateHelper.getMessage( InterceptorState.CLIENT_IN );
        Message outMessage = message.getExchange().getOutMessage();
        doReturn( String.format( "http://%s:%d/rest/v1/peer", Common.LOCAL_HOST_IP, PORT ) ).when( outMessage )
                                                                                            .get( Message
                                                                                                    .ENDPOINT_ADDRESS );
        doReturn( PEER_ID ).when( interceptor ).getPeerIdByIp( Common.LOCAL_HOST_IP );
        doReturn( peer ).when( peerManager ).getPeer( PEER_ID );
        doReturn( peerInfo ).when( peer ).getPeerInfo();
        doReturn( PORT ).when( peerInfo ).getPublicSecurePort();
        doReturn( localPeer ).when( peerManager ).getLocalPeer();


        interceptor.handleMessage( message );

        verify( interceptor ).getPeerIdByIp( Common.LOCAL_HOST_IP );
        verify( interceptor ).handlePeerMessage( PEER_ID, message );

        doReturn( String.format( "http://%s:%d/rest/v1/env/%s/", Common.LOCAL_HOST_IP, PORT, ENV_ID ) ).when( outMessage )
                                                                                           .get( Message
                                                                                                   .ENDPOINT_ADDRESS );

        interceptor.handleMessage( message );

        verify( interceptor ).handleEnvironmentMessage( PEER_ID,ENV_ID, message );
    }
}

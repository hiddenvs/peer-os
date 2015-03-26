package org.safehaus.subutai.core.peer.api;


import java.util.Set;
import java.util.UUID;

import org.safehaus.subutai.common.metric.ResourceHostMetric;
import org.safehaus.subutai.common.peer.ContainerHost;
import org.safehaus.subutai.common.peer.Host;


/**
 * Resource host interface.
 */
public interface ResourceHost extends Host
{
    public ResourceHostMetric getHostMetric() throws ResourceHostException;

    public Set<ContainerHost> getContainerHosts();

    public ContainerHost getContainerHostByName( String hostname ) throws HostNotFoundException;

    public ContainerHost getContainerHostById( UUID id ) throws HostNotFoundException;

    public void startContainerHost( ContainerHost containerHost ) throws ResourceHostException;

    public void stopContainerHost( ContainerHost containerHost ) throws ResourceHostException;

    public void destroyContainerHost( ContainerHost containerHost ) throws ResourceHostException;

    public ContainerState getContainerHostState( final ContainerHost container ) throws ResourceHostException;

    public ContainerHost createContainer( String templateName, String hostname, int timeout )
            throws ResourceHostException;

    public ContainerHost createContainer( String templateName, String hostname, String ip, int vlan, String gateway,
                                          int timeout ) throws ResourceHostException;
}

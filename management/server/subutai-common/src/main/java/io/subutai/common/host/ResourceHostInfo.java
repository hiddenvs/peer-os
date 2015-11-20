package io.subutai.common.host;


import java.util.Set;


/**
 * Host info. Can contain info about resource host or management host
 */
public interface ResourceHostInfo extends HostInfo
{
    /**
     * returns hosted containers
     */
    public Set<ContainerHostInfo> getContainers();

    public InstanceType getInstanceType();
}

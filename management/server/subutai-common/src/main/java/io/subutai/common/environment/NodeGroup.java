package io.subutai.common.environment;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.subutai.common.host.HostId;
import io.subutai.common.peer.PeerId;
import io.subutai.common.protocol.PlacementStrategy;


/**
 * Node group
 */
public class NodeGroup
{
    private String name;
    private String templateName;
    private ContainerType type;
    private int numberOfContainers;
    private int sshGroupId;
    private int hostsGroupId;
    private PlacementStrategy containerPlacementStrategy;
    private String peerId;
    private String hostId;


    public NodeGroup( final String name, final String templateName, final int numberOfContainers, final int sshGroupId,
                      final int hostsGroupId, final PlacementStrategy containerPlacementStrategy )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( name ), "Invalid node group name" );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( templateName ), "Invalid template name" );
        Preconditions.checkArgument( numberOfContainers > 0, "Number of containers must be greater than 0" );
        Preconditions.checkNotNull( containerPlacementStrategy, "Invalid container placement strategy" );


        this.name = name;
        this.templateName = templateName;
        this.numberOfContainers = numberOfContainers;
        this.sshGroupId = sshGroupId;
        this.hostsGroupId = hostsGroupId;
        this.containerPlacementStrategy = containerPlacementStrategy;
    }


    public NodeGroup( final String name, final String templateName, final ContainerType type,
                      final int numberOfContainers, final int sshGroupId, final int hostsGroupId)
    {
        this.name = name;
        this.templateName = templateName;
        this.type = type;
        this.numberOfContainers = numberOfContainers;
        this.sshGroupId = sshGroupId;
        this.hostsGroupId = hostsGroupId;
    }


    public String getName()
    {
        return name;
    }


    public int getNumberOfContainers()
    {
        return numberOfContainers;
    }


    public String getTemplateName()
    {
        return templateName;
    }


    public PlacementStrategy getContainerPlacementStrategy()
    {
        return containerPlacementStrategy;
    }


    public int getSshGroupId()
    {
        return sshGroupId;
    }


    public int getHostsGroupId()
    {
        return hostsGroupId;
    }


    public ContainerType getType()
    {
        return type;
    }


    public String getPeerId()
    {
        return peerId;
    }


    public String getHostId()
    {
        return hostId;
    }
}

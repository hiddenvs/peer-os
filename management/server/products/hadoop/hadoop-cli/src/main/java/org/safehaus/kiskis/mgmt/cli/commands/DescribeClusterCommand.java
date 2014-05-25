package org.safehaus.kiskis.mgmt.cli.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.safehaus.kiskis.mgmt.api.hadoop.Config;
import org.safehaus.kiskis.mgmt.api.hadoop.Hadoop;
import org.safehaus.subutai.shared.protocol.Agent;


/**
 * Displays the last log entries
 */
@Command(scope = "hadoop", name = "describe-clusters", description = "Shows the details of Hadoop cluster")
public class DescribeClusterCommand extends OsgiCommandSupport {

    private Hadoop hadoopManager;

    public Hadoop getHadoopManager() {
        return hadoopManager;
    }

    public void setHadoopManager(Hadoop hadoopManager) {
        this.hadoopManager = hadoopManager;
    }

    @Argument(index = 0, name = "clusterName", required = true, multiValued = false, description = "The name of the Hadoop cluster")
    String clusterName;


    protected Object doExecute() {
        Config config = hadoopManager.getCluster(clusterName);
        if (config != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cluster name: ").append(config.getClusterName()).append("\n");
            sb.append("Domain name: ").append(config.getDomainName()).append("\n");
            sb.append("All nodes:").append("\n");
            for (Agent agent : config.getAllNodes()) {
                sb.append("Hostname: ").append(agent.getHostname()).append("\n");
            }
            sb.append("Slave nodes:").append("\n");
            for (Agent agent : config.getAllSlaveNodes()) {
                sb.append("Hostname: ").append(agent.getHostname()).append("\n");
            }
            sb.append("Data nodes:").append("\n");
            for (Agent agent : config.getDataNodes()) {
                sb.append("Hostname: ").append(agent.getHostname()).append("\n");
            }
            sb.append("Task trackers:").append("\n");
            for (Agent agent : config.getTaskTrackers()) {
                sb.append("Hostname: ").append(agent.getHostname()).append("\n");
            }
            Agent jt = config.getJobTracker();
            sb.append("Job tracker").append("\n");
            sb.append("Hostname:").append(jt.getHostname()).append("\n");
            sb.append("IPs:").append(jt.getListIP()).append("\n");
            sb.append("MAC address:").append(jt.getMacAddress()).append("\n");
            sb.append("Parent hostname:").append(jt.getParentHostName()).append("\n");
            sb.append("UUID:").append(jt.getUuid()).append("\n");
            System.out.println(sb.toString());
        } else System.out.println("No clusters found...");

        return null;
    }
}

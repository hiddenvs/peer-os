package org.safehaus.kiskis.mgmt.impl.flume.handler;

import java.util.*;

import org.safehaus.kiskis.mgmt.api.flume.Config;
import org.safehaus.kiskis.mgmt.impl.flume.*;
import org.safehaus.subutai.api.commandrunner.AgentResult;
import org.safehaus.subutai.api.commandrunner.Command;
import org.safehaus.subutai.api.commandrunner.RequestBuilder;
import org.safehaus.subutai.shared.operation.AbstractOperationHandler;
import org.safehaus.subutai.shared.operation.ProductOperation;
import org.safehaus.subutai.shared.protocol.Agent;

public class DestroyNodeHandler extends AbstractOperationHandler<FlumeImpl> {

    private final String hostname;

    public DestroyNodeHandler(FlumeImpl manager, String clusterName, String hostname) {
        super(manager, clusterName);
        this.hostname = hostname;
        this.productOperation = manager.getTracker().createProductOperation(
                Config.PRODUCT_KEY, "Remove node from cluster: " + hostname);
    }

    @Override
    public void run() {
        ProductOperation po = productOperation;
        Config config = manager.getCluster(clusterName);
        if(config == null) {
            po.addLogFailed("Cluster does not exist: " + clusterName);
            return;
        }

        Agent agent = manager.getAgentManager().getAgentByHostname(hostname);
        if(agent == null) {
            po.addLogFailed("Node is not connected: " + hostname);
            return;
        }
        if(config.getNodes().size() == 1) {
            po.addLogFailed("This is the last node in the cluster. Destroy cluster instead");
            return;
        }

        po.addLog("Uninstalling Flume...");
        Command cmd = manager.getCommandRunner().createCommand(
                new RequestBuilder(Commands.make(CommandType.PURGE)),
                new HashSet<>(Arrays.asList(agent)));
        manager.getCommandRunner().runCommand(cmd);

        if(cmd.hasCompleted()) {
            AgentResult res = cmd.getResults().get(agent.getUuid());
            if(res.getExitCode() != null && res.getExitCode() == 0)
                if(res.getStdOut().contains("ksks-flume is not installed"))
                    po.addLog("Flume is not installed on " + agent.getHostname());
                else
                    po.addLog("Flume removed from " + agent.getHostname());
            else
                po.addLog(String.format("Error on node %s: %s",
                        agent.getHostname(), res.getStdErr()));

            config.getNodes().remove(agent);

            po.addLog("Updating db...");
            if(manager.getDbManager().saveInfo(Config.PRODUCT_KEY, config.getClusterName(), config))
                po.addLogDone("Cluster info updated");
            else
                po.addLogFailed("Failed to save cluster info");
        } else {
            po.addLog(cmd.getAllErrors());
            po.addLogFailed("Uninstallation failed");
        }
    }

}

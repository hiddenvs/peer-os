/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.ui.zookeeper;

import com.vaadin.ui.Component;
import org.safehaus.subutai.api.agentmanager.AgentManager;
import org.safehaus.kiskis.mgmt.api.hadoop.Hadoop;
import org.safehaus.subutai.api.tracker.Tracker;
import org.safehaus.kiskis.mgmt.api.zookeeper.Config;
import org.safehaus.kiskis.mgmt.api.zookeeper.Zookeeper;
import org.safehaus.subutai.server.ui.services.Module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dilshat
 */
public class ZookeeperUI implements Module {

    private static Zookeeper manager;
    private static AgentManager agentManager;
    private static Tracker tracker;
    private static Hadoop hadoopManager;
    private static ExecutorService executor;

    public ZookeeperUI(AgentManager agentManager, Tracker tracker, Zookeeper manager, Hadoop hadoopManager) {
        ZookeeperUI.agentManager = agentManager;
        ZookeeperUI.tracker = tracker;
        ZookeeperUI.manager = manager;
        ZookeeperUI.hadoopManager = hadoopManager;
    }

    public static Hadoop getHadoopManager() {
        return hadoopManager;
    }

    public static Tracker getTracker() {
        return tracker;
    }

    public static Zookeeper getManager() {
        return manager;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static AgentManager getAgentManager() {
        return agentManager;
    }

    public void init() {
        executor = Executors.newCachedThreadPool();
    }

    public void destroy() {
        manager = null;
        agentManager = null;
        tracker = null;
        hadoopManager = null;
        executor.shutdown();
    }

    public String getName() {
        return Config.PRODUCT_KEY;
    }

    public Component createComponent() {
        return new Form();
    }

}

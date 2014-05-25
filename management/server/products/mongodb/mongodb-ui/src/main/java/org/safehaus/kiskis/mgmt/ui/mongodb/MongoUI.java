/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.ui.mongodb;

import com.vaadin.ui.Component;
import org.safehaus.subutai.api.agentmanager.AgentManager;
import org.safehaus.kiskis.mgmt.api.mongodb.Config;
import org.safehaus.kiskis.mgmt.api.mongodb.Mongo;
import org.safehaus.kiskis.mgmt.api.tracker.Tracker;
import org.safehaus.kiskis.mgmt.server.ui.services.Module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dilshat
 */
public class MongoUI implements Module {

    private static Mongo mongoManager;
    private static AgentManager agentManager;
    private static ExecutorService executor;
    private static Tracker tracker;

    public MongoUI(AgentManager agentManager, Mongo mongoManager, Tracker tracker) {
        MongoUI.agentManager = agentManager;
        MongoUI.mongoManager = mongoManager;
        MongoUI.tracker = tracker;
    }

    public static Tracker getTracker() {
        return tracker;
    }

    public static Mongo getMongoManager() {
        return mongoManager;
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
        tracker = null;
        mongoManager = null;
        agentManager = null;
        executor.shutdown();
    }

    public String getName() {
        return Config.PRODUCT_KEY;
    }

    public Component createComponent() {
        return new MongoForm();
    }

}

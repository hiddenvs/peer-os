package org.safehaus.kiskis.mgmt.impl.hive.query;

import org.safehaus.kiskis.mgmt.api.hive.query.Config;

import java.util.List;
import java.util.UUID;

public class HiveQueryImpl extends HiveQueryBase {

    @Override
    public boolean save(Config config) {
        return dbManager.saveInfo(Config.PRODUCT_KEY, config.getClusterName(), config);
    }

    @Override
    public List<Config> load() {
        return dbManager.getInfo(Config.PRODUCT_KEY, Config.class);
    }


    @Override
    public UUID installCluster(Config config) {
        return null;
    }

    @Override
    public UUID uninstallCluster(String clusterName) {
        return null;
    }

    @Override
    public List<Config> getClusters() {
        return null;
    }

    @Override
    public Config getCluster(String clusterName) {
        return null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.api.mahout;

import org.safehaus.subutai.shared.protocol.ApiBase;

import java.util.UUID;

/**
 * @author dilshat
 */
public interface Mahout extends ApiBase<Config> {

    public UUID addNode(String clusterName, String lxcHostname);

    public UUID destroyNode(String clusterName, String lxcHostname);

}

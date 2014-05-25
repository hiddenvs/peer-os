/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.ui.spark.wizard;

import com.vaadin.ui.*;
import org.safehaus.kiskis.mgmt.api.spark.Config;
import org.safehaus.subutai.server.ui.MgmtApplication;
import org.safehaus.subutai.shared.protocol.Agent;
import org.safehaus.kiskis.mgmt.ui.spark.SparkUI;

import java.util.UUID;

/**
 * @author dilshat
 */
public class VerificationStep extends Panel {

    public VerificationStep(final Wizard wizard) {

        setSizeFull();

        GridLayout grid = new GridLayout(1, 5);
        grid.setSpacing(true);
        grid.setMargin(true);
        grid.setSizeFull();

        Label confirmationLbl = new Label("<strong>Please verify the installation settings "
                + "(you may change them by clicking on Back button)</strong><br/>");
        confirmationLbl.setContentMode(Label.CONTENT_XHTML);

        ConfigView cfgView = new ConfigView("Installation configuration");
        cfgView.addStringCfg("Cluster Name", wizard.getConfig().getClusterName());
        cfgView.addStringCfg("Master Node", wizard.getConfig().getMasterNode().getHostname());
        for (Agent agent : wizard.getConfig().getSlaveNodes()) {
            cfgView.addStringCfg("Slave nodes", agent.getHostname() + "");
        }

        Button install = new Button("Install");
        install.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                UUID trackID = SparkUI.getSparkManager().installCluster(wizard.getConfig());
                MgmtApplication.showProgressWindow(Config.PRODUCT_KEY, trackID, null);
                wizard.init();
            }
        });

        Button back = new Button("Back");
        back.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                wizard.back();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(back);
        buttons.addComponent(install);

        grid.addComponent(confirmationLbl, 0, 0);

        grid.addComponent(cfgView.getCfgTable(), 0, 1, 0, 3);

        grid.addComponent(buttons, 0, 4);

        addComponent(grid);

    }

}

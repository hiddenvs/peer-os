/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.subutai.ui.shark.wizard;

import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.safehaus.subutai.shared.protocol.FileUtil;
import org.safehaus.subutai.ui.shark.SharkUI;

/**
 * @author dilshat
 */
public class WelcomeStep extends Panel {

	public WelcomeStep(final Wizard wizard) {

		setSizeFull();

		GridLayout grid = new GridLayout(10, 6);
		grid.setSpacing(true);
		grid.setMargin(true);
		grid.setSizeFull();

		Label welcomeMsg = new Label("<center><h2>Welcome to Shark Installation Wizard!</h2>");
		welcomeMsg.setContentMode(ContentMode.HTML);
		grid.addComponent(welcomeMsg, 3, 1, 6, 2);

		Label logoImg = new Label();
		logoImg.setIcon(new FileResource(FileUtil.getFile(SharkUI.MODULE_IMAGE, this)));
		logoImg.setContentMode(ContentMode.HTML);
		logoImg.setHeight(363, Unit.PIXELS);
		logoImg.setWidth(88, Unit.PIXELS);
		grid.addComponent(logoImg, 1, 3, 2, 5);

		Button next = new Button("Start");
		next.addStyleName("default");
		next.setWidth(100, Unit.PIXELS);
		grid.addComponent(next, 6, 4, 6, 4);
		grid.setComponentAlignment(next, Alignment.BOTTOM_RIGHT);

		next.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				wizard.init();
				wizard.next();
			}
		});

		setContent(grid);
	}

}

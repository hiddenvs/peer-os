/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.server.ui.modules.hadoop.wizard;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import org.safehaus.kiskis.mgmt.shared.protocol.Task;
import org.safehaus.kiskis.mgmt.shared.protocol.Util;
import org.safehaus.kiskis.mgmt.shared.protocol.enums.TaskStatus;

/**
 * @author bahadyr
 */
public class Step3 extends Panel {

    private TextArea terminal;
    HadoopWizard parent;
    Button next;

    public Step3(final HadoopWizard hadoopWizard) {
        parent = hadoopWizard;

        setCaption("Welcome to Hadoop Cluster Installation");
        setSizeFull();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setHeight(100, Sizeable.UNITS_PERCENTAGE);
        verticalLayout.setMargin(true);

        GridLayout grid = new GridLayout(6, 10);
        grid.setSpacing(true);
        grid.setSizeFull();

        Panel panel = new Panel();
        Label menu = new Label("Cluster Install Wizard<br>"
                + " 1) Master Configurations<br>"
                + " 2) Slave Configurations<br>"
                + " 3) <font color=\"#f14c1a\"><strong>Installation</strong></font><br>");
        menu.setContentMode(Label.CONTENT_XHTML);
        panel.addComponent(menu);
        grid.addComponent(menu, 0, 0, 0, 5);
        grid.setComponentAlignment(panel, Alignment.TOP_CENTER);

        VerticalLayout verticalLayoutForm = new VerticalLayout();
        verticalLayoutForm.setSizeFull();
        verticalLayoutForm.setSpacing(true);

        terminal = new TextArea();
        terminal.setRows(20);
        terminal.setColumns(40);
        terminal.setImmediate(true);
        terminal.setWordwrap(false);
        verticalLayoutForm.addComponent(terminal);

        grid.addComponent(verticalLayoutForm, 1, 0, 5, 9);
        grid.setComponentAlignment(verticalLayoutForm, Alignment.MIDDLE_CENTER);

        next = new Button("Finish");
        next.setEnabled(false);
        next.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                parent.showNext();
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(next);

        verticalLayout.addComponent(grid);
        verticalLayout.addComponent(horizontalLayout);

        addComponent(verticalLayout);

        parent.hadoopInstallation.setPanel(this);
        parent.getHadoopInstallation().installHadoop();
    }

    public void addOutput(Task task, String stdResult) {
        if (task.getTaskStatus().compareTo(TaskStatus.SUCCESS) == 0) {
            if (!Util.isStringEmpty(stdResult) && !stdResult.equals("null")) {
                StringBuffer str = new StringBuffer();
                str.append(terminal.getValue());
                str.append("\n");
                str.append(task.getDescription());
                str.append(stdResult);
                terminal.setValue(str);
            }
        } else {
            if (!Util.isStringEmpty(stdResult) && !stdResult.equals("null")) {
                StringBuffer str = new StringBuffer();
                str.append(terminal.getValue());
                str.append("\n");
                str.append(task.getDescription());
                str.append(stdResult);
                terminal.setValue(str);
            }
        }
    }

    public void setCloseable() {
        next.setEnabled(true);
    }

    public void show(String notification){
        getWindow().showNotification(notification);
    }
}

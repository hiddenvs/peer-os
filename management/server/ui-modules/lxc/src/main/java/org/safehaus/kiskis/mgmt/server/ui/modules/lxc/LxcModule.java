package org.safehaus.kiskis.mgmt.server.ui.modules.lxc;


import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.safehaus.kiskis.mgmt.server.ui.modules.lxc.forms.LxcCloneForm;
import org.safehaus.kiskis.mgmt.server.ui.modules.lxc.forms.LxcManageForm;
import org.safehaus.kiskis.mgmt.server.ui.services.Module;
import org.safehaus.kiskis.mgmt.server.ui.services.ModuleService;
import org.safehaus.kiskis.mgmt.shared.protocol.*;
import org.safehaus.kiskis.mgmt.shared.protocol.api.CommandManagerInterface;
import org.safehaus.kiskis.mgmt.shared.protocol.api.ui.CommandListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LxcModule implements Module {

    private static final Logger LOG = Logger.getLogger(LxcModule.class.getName());
    private ModuleService service;
    private BundleContext context;
    public static final String MODULE_NAME = "LXC";

    public static class ModuleComponent extends CustomComponent implements CommandListener {
        private BundleContext context;
        private TabSheet commandsSheet;
        private LxcCloneForm cloneForm;
        private LxcManageForm manageForm;

        public ModuleComponent(BundleContext context) {
            this.context = context;

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setSpacing(true);
            verticalLayout.setSizeFull();

            commandsSheet = new TabSheet();
            commandsSheet.setStyleName(Runo.TABSHEET_SMALL);
            commandsSheet.setSizeFull();

            cloneForm = new LxcCloneForm();
            commandsSheet.addTab(cloneForm, "Clone");
            manageForm = new LxcManageForm();
            commandsSheet.addTab(manageForm, "Manage");

            verticalLayout.addComponent(commandsSheet);

            setCompositionRoot(verticalLayout);

            try {
                getCommandManager().addListener(this);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error in addListener", ex);
            }
        }

        @Override
        public synchronized void outputCommand(Response response) {
            if(response != null && response.getSource().equals(MODULE_NAME)){
                cloneForm.outputResponse(response);
                manageForm.outputResponse(response);
            }
        }

        @Override
        public synchronized String getName() {
            return MODULE_NAME;
        }

        private CommandManagerInterface getCommandManager() {
            ServiceReference reference = context
                    .getServiceReference(CommandManagerInterface.class.getName());
            return (CommandManagerInterface) context.getService(reference);
        }
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public Component createComponent() {
        return new ModuleComponent(context);
    }

    public void setModuleService(ModuleService service) {
        if(service != null){
            System.out.println(MODULE_NAME + " registering with ModuleService");
            this.service = service;
            this.service.registerModule(this);
        }
    }

    public void unsetModuleService(ModuleService service) {
        if(service != null){
            this.service.unregisterModule(this);
        }
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }
}
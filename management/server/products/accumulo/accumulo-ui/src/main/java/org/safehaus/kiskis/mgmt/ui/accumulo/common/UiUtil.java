package org.safehaus.kiskis.mgmt.ui.accumulo.common;


import org.safehaus.subutai.server.ui.MgmtApplication;
import org.safehaus.subutai.shared.protocol.Agent;
import org.safehaus.subutai.shared.protocol.Util;
import org.safehaus.kiskis.mgmt.ui.accumulo.AccumuloUI;

import com.google.common.base.Strings;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Window;


/**
 * Created by dilshat on 4/28/14.
 */
public class UiUtil {

    public static final String MASTER_PREFIX = "Master: ";
    public static final String GC_PREFIX = "GC: ";
    public static final String MONITOR_PREFIX = "Monitor: ";


    public static ComboBox getCombo( String title ) {
        ComboBox combo = new ComboBox( title );
        combo.setMultiSelect( false );
        combo.setImmediate( true );
        combo.setTextInputAllowed( false );
        combo.setRequired( true );
        combo.setNullSelectionAllowed( false );
        return combo;
    }


    public static TwinColSelect getTwinSelect( String title, String captionProperty, String leftTitle,
                                               String rightTitle, int rows ) {
        TwinColSelect twinColSelect = new TwinColSelect( title );
        twinColSelect.setItemCaptionPropertyId( captionProperty );
        twinColSelect.setRows( rows );
        twinColSelect.setMultiSelect( true );
        twinColSelect.setImmediate( true );
        twinColSelect.setLeftColumnCaption( leftTitle );
        twinColSelect.setRightColumnCaption( rightTitle );
        twinColSelect.setWidth( 100, Sizeable.UNITS_PERCENTAGE );
        twinColSelect.setRequired( true );
        return twinColSelect;
    }


    public static Table createTableTemplate( String caption, int size, final Component parent,
                                             boolean destroyButtonNeeded ) {
        final Table table = new Table( caption );
        table.addContainerProperty( "Host", String.class, null );
        table.addContainerProperty( "Check", Button.class, null );
        if ( destroyButtonNeeded ) {
            table.addContainerProperty( "Destroy", Button.class, null );
        }
        table.addContainerProperty( "Nodes state", Label.class, null );
        table.addContainerProperty( "Status", Embedded.class, null );
        table.setWidth( 100, Sizeable.UNITS_PERCENTAGE );
        table.setHeight( size, Sizeable.UNITS_PIXELS );
        table.setPageLength( 10 );
        table.setSelectable( false );
        table.setImmediate( true );

        table.addListener( new ItemClickEvent.ItemClickListener() {

            public void itemClick( ItemClickEvent event ) {
                if ( event.isDoubleClick() ) {
                    String lxcHostname =
                            ( String ) table.getItem( event.getItemId() ).getItemProperty( "Host" ).getValue();
                    lxcHostname = lxcHostname.replaceAll( MASTER_PREFIX, "" ).replaceAll( GC_PREFIX, "" )
                                             .replaceAll( MONITOR_PREFIX, "" );
                    Agent lxcAgent = AccumuloUI.getAgentManager().getAgentByHostname( lxcHostname );
                    if ( lxcAgent != null ) {
                        Window terminal = MgmtApplication.createTerminalWindow( Util.wrapAgentToSet( lxcAgent ) );
                        MgmtApplication.addCustomWindow( terminal );
                    }
                    else {
                        showMsg( "Agent is not connected", parent.getWindow() );
                    }
                }
            }
        } );
        return table;
    }


    public static void showMsg( String msg, Window window ) {
        if ( window != null && !Strings.isNullOrEmpty( msg ) ) {
            window.showNotification( msg );
        }
    }


    public static TextField getTextField( String caption, String prompt, int maxLength ) {
        TextField textField = new TextField( caption );
        textField.setInputPrompt( prompt );
        textField.setMaxLength( maxLength );
        textField.setRequired( true );
        return textField;
    }


    public static void clickAllButtonsInTable( Table table, String buttonCaption ) {
        for ( Object o : table.getItemIds() ) {
            int rowId = ( Integer ) o;
            Item row = table.getItem( rowId );
            Button checkBtn = ( Button ) ( row.getItemProperty( buttonCaption ).getValue() );
            checkBtn.click();
        }
    }
}

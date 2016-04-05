package de.abas.documentation.advanced.eventhandling;

import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.FieldEventHandler;
import de.abas.erp.axi2.event.FieldEvent;
import de.abas.erp.axi2.type.FieldEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.customer.CustomerContactEditor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;
import de.abas.jfop.base.Color;

// TODO: Remodel example to use standard fields
@EventHandler(head = CustomerContactEditor.class)
@RunFopWith(EventHandlerRunner.class)
public class CustomerContactEventHandlerAXI2 {

	@FieldEventHandler(field = "yfirstname", type = FieldEventType.EXIT)
	public void yfirstnameExit(FieldEvent event, ScreenControl screenControl, DbContext ctx, CustomerContactEditor head)
			throws EventException {
		if (CustomerContactEditor.META.yfirstname.isModified(head)) {
			screenControl.setColor(head, CustomerContactEditor.META.addr, Color.DEFAULT, Color.LIGHT_ORANGE);
			screenControl.setColor(head, CustomerContactEditor.META.descrOperLang, Color.DEFAULT, Color.LIGHT_ORANGE);
			screenControl.setColor(head, CustomerContactEditor.META.contactPerson, Color.DEFAULT, Color.LIGHT_ORANGE);
		}
	}

	@FieldEventHandler(field = "ysurname", type = FieldEventType.EXIT)
	public void ysurnameExit(FieldEvent event, ScreenControl screenControl, DbContext ctx, CustomerContactEditor head)
			throws EventException {
		if (CustomerContactEditor.META.yfirstname.isModified(head)) {
			screenControl.setColor(head, CustomerContactEditor.META.addr, Color.DEFAULT, Color.LIGHT_ORANGE);
			screenControl.setColor(head, CustomerContactEditor.META.descrOperLang, Color.DEFAULT, Color.LIGHT_ORANGE);
			screenControl.setColor(head, CustomerContactEditor.META.contactPerson, Color.DEFAULT, Color.LIGHT_ORANGE);
		}
	}

}

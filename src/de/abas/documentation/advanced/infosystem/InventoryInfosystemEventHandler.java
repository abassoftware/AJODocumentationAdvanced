package de.abas.documentation.advanced.infosystem;

import de.abas.erp.jfop.rt.api.annotation.RunFopWith;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.db.infosystem.custom.ow1.InventoryInfosystem;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.event.ScreenEvent;
import de.abas.erp.db.DbContext;
import de.abas.erp.axi.screen.ScreenControl;

@EventHandler(head = InventoryInfosystem.class, row = InventoryInfosystem.Row.class)

@RunFopWith(EventHandlerRunner.class)

public class InventoryInfosystemEventHandler {

	@ScreenEventHandler(type = ScreenEventType.ENTER)
	public void screenEnter(ScreenEvent event, ScreenControl screenControl, DbContext ctx, InventoryInfosystem head) throws EventException{
		// TODO Auto-generated method stub
	}

}

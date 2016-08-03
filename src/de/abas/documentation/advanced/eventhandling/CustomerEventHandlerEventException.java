package de.abas.documentation.advanced.eventhandling;

import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.FieldEventHandler;
import de.abas.erp.axi2.event.FieldEvent;
import de.abas.erp.axi2.type.FieldEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = CustomerEditor.class)
@RunFopWith(EventHandlerRunner.class)
public class CustomerEventHandlerEventException {

	@FieldEventHandler(field = "descrOperLang", type = FieldEventType.VALIDATION)
	public void descrOperLangValidation(FieldEvent event, ScreenControl screenControl, DbContext ctx,
			CustomerEditor head) throws EventException {
		if (!head.getDescrOperLang().matches("[A-Za-z0-9ƒд÷ц№ья ,+-]+")) {
			throw new EventException("verify input", 1);
		} else {
			throw new EventException("all right", 0);
		}
	}

}

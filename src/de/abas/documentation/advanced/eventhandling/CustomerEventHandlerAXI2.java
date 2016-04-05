package de.abas.documentation.advanced.eventhandling;

import java.util.ArrayList;

import de.abas.eks.jfop.annotation.Stateful;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.FieldEventHandler;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.event.ButtonEvent;
import de.abas.erp.axi2.event.FieldEvent;
import de.abas.erp.axi2.event.ScreenEvent;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.axi2.type.FieldEventType;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.Query;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.schema.sales.SalesOrder;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

// TODO: Remodel example to use standard field
@EventHandler(head = CustomerEditor.class)
@RunFopWith(EventHandlerRunner.class)
@Stateful
public class CustomerEventHandlerAXI2 {

	ArrayList<String> arrayList;
	int eventIndex = 0;

	@ScreenEventHandler(type = ScreenEventType.ENTER)
	public void screenEnter(ScreenEvent event, ScreenControl screenControl, DbContext ctx, CustomerEditor head)
			throws EventException {
		arrayList = new ArrayList<String>();
	}

	@ScreenEventHandler(type = ScreenEventType.VALIDATION)
	public void screenValidation(ScreenEvent event, ScreenControl screenControl, DbContext ctx, CustomerEditor head)
			throws EventException {
		for (final String string : arrayList) {
			ctx.out().println(string);
		}
	}

	@ButtonEventHandler(field = "ydisplaysalesorder", type = ButtonEventType.AFTER)
	public void ydisplaysalesorderAfter(ButtonEvent event, ScreenControl screenControl, DbContext ctx,
			CustomerEditor head) throws EventException {
		final Query<SalesOrder> query = ctx.createQuery(
				SelectionBuilder.create(SalesOrder.class).add(Conditions.eq(SalesOrder.META.customer, head)).build());
		for (final SalesOrder salesOrder : query) {
			ctx.out().println(salesOrder.getIdno() + "- " + salesOrder.getSwd());
		}
	}

	@FieldEventHandler(field = "zipCode", type = FieldEventType.EXIT)
	public void zipCodeExit(FieldEvent event, ScreenControl screenControl, DbContext ctx, CustomerEditor head)
			throws EventException {
		final String newValue = head.getZipCode();
		final String fieldName = event.getFieldName();
		arrayList.add(fieldName + "->" + eventIndex++ + " - " + newValue);

	}

	@FieldEventHandler(field = "zipCode", type = FieldEventType.VALIDATION)
	public void zipCodeValidation(FieldEvent event, ScreenControl screenControl, DbContext ctx, CustomerEditor head)
			throws EventException {
		if (!head.getZipCode().matches("[1-9][0-9]{4}")) {
			throw new EventException("invalid zipcode", 1);
		}
	}

}

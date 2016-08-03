package de.abas.documentation.advanced.eventhandling;

import java.util.Calendar;
import java.util.Locale;

import de.abas.documentation.advanced.utilities.AbasDateUtilities;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.FieldEventHandler;
import de.abas.erp.axi2.event.FieldEvent;
import de.abas.erp.axi2.type.FieldEventType;
import de.abas.erp.common.type.AbasDate;
import de.abas.erp.common.type.AbasDateTime;
import de.abas.erp.common.type.AbasDuration;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.part.Product;
import de.abas.erp.db.schema.part.SelectablePart;
import de.abas.erp.db.schema.sales.PackingSlipEditor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = PackingSlipEditor.class, row = PackingSlipEditor.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class PackingSlipEventHandlerAXI2 {

	@FieldEventHandler(field = "product", type = FieldEventType.EXIT, table = true)
	public void productExit(FieldEvent event, ScreenControl screenControl, DbContext ctx, PackingSlipEditor head,
			PackingSlipEditor.Row currentRow) throws EventException {
		final SelectablePart selectablePart = currentRow.getProduct();
		if (selectablePart instanceof Product) {
			final Product product = (Product) selectablePart;
			final AbasDuration warrantyPer = product.getWarrantyPer();
			if (warrantyPer != null) {
				currentRow.setYtwadate(calculateWarrantyDate(ctx, head.getDateFrom(), warrantyPer));
			}
		}
	}

	private AbasDate calculateWarrantyDate(DbContext ctx, AbasDate dateFrom, AbasDuration warrantyPer) {
		final Calendar calendar = Calendar.getInstance(Locale.GERMANY);
		calendar.setTime(
				new AbasDateUtilities().addDuration(ctx, new AbasDateTime(dateFrom.toDate()), warrantyPer).toDate());
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new AbasDate(calendar.getTime());
	}

}

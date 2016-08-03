package de.abas.documentation.advanced.record.objectsfromxml;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.part.ProductEditor;

public class MakeSubBomProducts extends AbstractAjoAccess {

	DbContext dbContext = getDbContext();
	ProductEditor productEditor;
	String[] swds = { "PLATE", "CUP", "DESK", "CABLETUN", "BOX" };

	@Override
	public int run(String[] args) {
		for (final String swd : swds) {
			productEditor = dbContext.newObject(ProductEditor.class);
			productEditor.setSwd(swd);
			productEditor.commit();
		}

		return 0;
	}

}

package de.abas.documentation.advanced.record.transaction;

import java.util.List;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.EditorAction;
import de.abas.erp.db.Transaction;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;

public class ChangeCustomerCodeTransaction extends AbstractAjoAccess {

	public static void main(String[] args) {
		new ChangeCustomerCodeTransaction().runClientProgram(args);
	}

	DbContext dbContext = getDbContext();
	final Transaction transaction = dbContext.getTransaction();
	CustomerEditor customerEditor;

	@Override
	public int run(String[] args) {
		final List<Customer> customers = dbContext.createQuery(SelectionBuilder.create(Customer.class)
				.add(Conditions.between(Customer.META.idno, "70000", "70020")).build()).execute();

		transaction.begin();
		try {
			for (final Customer customer : customers) {
				if (customer.getCode().isEmpty()) {
					customerEditor = customer.createEditor();
					customerEditor.open(EditorAction.UPDATE);
					customerEditor.setCode("code: " + customer.getIdno());
					customerEditor.commit();
					dbContext.out().println("changed: " + customer.getIdno());
				} else {
					transaction.rollback();
					throw new Exception("transaction rollback");
				}
			}
			dbContext.out().println("transaction commit");
			transaction.commit();
			return 0;
		} catch (final Exception e) {
			dbContext.out().println(e.getMessage());
			return 1;
		} finally {
			if (customerEditor != null) {
				if (customerEditor.active()) {
					customerEditor.abort();
				}
			}
		}
	}

}

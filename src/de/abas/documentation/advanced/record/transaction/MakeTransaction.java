package de.abas.documentation.advanced.record.transaction;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.SelectableObject;
import de.abas.erp.db.Transaction;
import de.abas.erp.db.schema.company.Summary;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.customer.CustomerContactEditor;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.schema.part.Product;
import de.abas.erp.db.schema.sales.Quotation;
import de.abas.erp.db.schema.sales.QuotationEditor;
import de.abas.erp.db.schema.sales.QuotationEditor.Row;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.db.util.QueryUtil;

public class MakeTransaction extends AbstractAjoAccess {

	public static void main(String[] args) {
		new MakeTransaction().runClientProgram(args);
	}

	DbContext dbContext = getDbContext();

	CustomerEditor customerEditor;
	CustomerContactEditor customerContactEditor;
	QuotationEditor quotationEditor;

	@Override
	public int run(String[] args) {
		final Transaction transaction = dbContext.getTransaction();
		transaction.begin();
		try {
			final Customer customer = createNewCustomer();
			final CustomerContact customerContact = createNewCustomerContact(customer);
			createNewQuotation(customer, customerContact);
			transaction.commit();
			dbContext.out().println("Transaction successfully committed");
		} catch (final Exception e) {
			dbContext.out().println(e.getMessage());
			transaction.rollback();
			dbContext.out().println("Transaction rollback completed");
		}

		return 0;

	}

	private Customer createNewCustomer() {
		customerEditor = dbContext.newObject(CustomerEditor.class);
		customerEditor.setSwd("Muster");
		customerEditor.setAddr("Max Mustermann");
		customerEditor.setStreet("Ludwigstr. 66");
		customerEditor.setZipCode("67165");
		customerEditor.setTown("Waldsee");
		customerEditor.setDescr(
				customerEditor.getAddr() + " " + customerEditor.getZipCode() + " " + customerEditor.getTown());
		customerEditor.commit();
		dbContext.out().println("new customer: " + customerEditor.objectId().getIdno());
		return customerEditor.objectId();
	}

	private CustomerContact createNewCustomerContact(Customer customer) {
		customerContactEditor = dbContext.newObject(CustomerContactEditor.class);
		// don't fill in 'swd', to generate an error
		// -> Exception will be thrown
		customerContactEditor.setSwd("CUSCON1");
		customerContactEditor.setCompanyARAP(customer);
		customerContactEditor.setSalutation(getObject(Summary.class, "AHERR"));
		customerContactEditor.setContactPerson("Silber");
		customerContactEditor.setFunctionAddressee("Production");
		customerContactEditor
				.setDescr(customerContactEditor.getDescr() + ", Herr " + customerContactEditor.getContactPerson());
		customerContactEditor
				.setAddr(customerContactEditor.getAddr() + "\nHerr " + customerContactEditor.getContactPerson());
		customerContactEditor.commit();
		dbContext.out().println("new customer contact: " + customerContactEditor.objectId().getIdno());
		return customerContactEditor.objectId();
	}

	private Quotation createNewQuotation(Customer customer, CustomerContact customerContact) {
		quotationEditor = dbContext.newObject(QuotationEditor.class);
		quotationEditor.setCustomer(customer);
		quotationEditor.setGoodsRecipient(customerContact); // Items
		final Row appendRow = quotationEditor.table().appendRow();
		appendRow.setProduct(getObject(Product.class, "TRACTOR"));
		appendRow.setUnitQty(1);
		quotationEditor.commit();
		dbContext.out().println("new quotation: " + quotationEditor.objectId().getIdno());
		return quotationEditor.objectId();
	}

	private <C extends SelectableObject> C getObject(Class<C> className, String swd) {
		return QueryUtil.getFirst(dbContext,
				SelectionBuilder.create(className).add(Conditions.starts("swd", swd)).build());
	}
}
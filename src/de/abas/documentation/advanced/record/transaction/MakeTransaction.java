package de.abas.documentation.advanced.record.transaction;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.common.type.IdImpl;
import de.abas.erp.db.Transaction;
import de.abas.erp.db.exception.DBRuntimeException;
import de.abas.erp.db.schema.company.Summary;
import de.abas.erp.db.schema.customer.CustomerContactEditor;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.schema.part.Product;
import de.abas.erp.db.schema.sales.QuotationEditor;

public class MakeTransaction extends AbstractAjoAccess {

	/**
	 * Instantiates MakeTransaction class and runs its run()-method as a client
	 * program.
	 *
	 * @param args Method arguments
	 */
	public static void main(String[] args) {
		MakeTransaction makeTransaction = new MakeTransaction();
		makeTransaction.runClientProgram(args);
	}

	@Override
	public void run(String[] args) {
		// gets and begins transaction
		Transaction transaction = getDbContext().getTransaction();
		transaction.begin();
		CustomerEditor newCustomer = null;
		CustomerContactEditor newCustomerContact = null;
		QuotationEditor newQuotation = null;
		try {
			// create new customer
			newCustomer = createNewCustomer(newCustomer);
			// create new customer contact
			newCustomerContact =
					createNewCustomerContact(newCustomerContact, newCustomer);
			// create new quotation
			newQuotation =
					createNewQuotation(newQuotation, newCustomerContact,
							newCustomer);
			transaction.commit();
			getDbContext().out().println("Transaction successfully committed.");
		}
		catch (TransactionCompletionException e) {
			getDbContext().out().println(e.getMessage());
			transaction.rollback();
			getDbContext().out().println("Rollback completed.");
		}
	}

	/**
	 * Creates new Customer.
	 *
	 * @param newCustomer The previously created CustomerEditor instance.
	 * @return The newly created customer.
	 * @throws TransactionCompletionException Thrown if an error occurs and the
	 * transaction has to be rolled back.
	 */
	protected CustomerEditor createNewCustomer(CustomerEditor newCustomer)
			throws TransactionCompletionException {
		// gets instance of CustomerEditor
		newCustomer = getDbContext().newObject(CustomerEditor.class);
		// sets fields for new customer
		newCustomer.setSwd("Mustermann");
		newCustomer.setAddr("Max Mustermann");
		newCustomer.setStreet("Ludwigstraﬂe 66");
		newCustomer.setZipCode("67165");
		newCustomer.setTown("Waldsee");
		newCustomer.setDescr(newCustomer.getAddr() + " "
				+ newCustomer.getZipCode() + " " + newCustomer.getTown());
		// commits and reopens editor
		try {
			newCustomer.commit();
		}
		catch (DBRuntimeException e) {
			newCustomer.abort();
			throw new TransactionCompletionException(e);
		}
		// returns the new customer
		return newCustomer;
	}

	/**
	 * Creates new Customer Contact.
	 *
	 * @param newCustomerContact The previously created CustomerContactEditor
	 * instance.
	 * @param newCustomer The previously created CustomerEditor instance.
	 * @return The newly created customer contact
	 * @throws TransactionCompletionException Thrown if an error occurs and the
	 * transaction has to be rolled back.
	 */
	protected CustomerContactEditor
			createNewCustomerContact(CustomerContactEditor newCustomerContact,
					CustomerEditor newCustomer)
					throws TransactionCompletionException {
		// gets instance of CustomerContactEditor
		newCustomerContact =
				getDbContext().newObject(CustomerContactEditor.class);
		// sets fields for new customer contact
		// newCustomerContact.setSwd("SILBER");
		newCustomerContact.setCompanyARAP(newCustomer);
		newCustomerContact.setSalutation(getDbContext().load(Summary.class,
				new IdImpl("(173,12,0)")));
		newCustomerContact.setContactPerson("Silber");
		newCustomerContact.setFunctionAddressee("Production");
		newCustomerContact.setDescr(newCustomerContact.getDescr() + ", Herr "
				+ newCustomerContact.getContactPerson());
		newCustomerContact.setAddr(newCustomerContact.getAddr() + "\nHerr "
				+ newCustomerContact.getContactPerson());
		// commits editor
		try {
			newCustomerContact.commit();
		}
		catch (DBRuntimeException e) {
			newCustomerContact.abort();
			throw new TransactionCompletionException(e);
		}
		// returns the new customer contact
		return newCustomerContact;
	}

	/**
	 * Creates new Quotation.
	 * 
	 * @param newQuotation The previously created QuotationEditor instance.
	 * @param newCustomerContact The previously created CustomerContactEditor
	 * instance.
	 * @param newCustomer The previously created CustomerEditor instance.
	 * @return The newly created quotation.
	 * @throws TransactionCompletionException Thrown if an error occurs and the
	 * transaction has to be rolled back.
	 */
	protected QuotationEditor
			createNewQuotation(QuotationEditor newQuotation,
					CustomerContactEditor newCustomerContact,
					CustomerEditor newCustomer)
					throws TransactionCompletionException {
		// gets instance of QuotationEditor
		newQuotation = getDbContext().newObject(QuotationEditor.class);
		// sets fields for new quotation
		newQuotation.setCustomer(newCustomerContact);
		QuotationEditor.Row appendRow = newQuotation.table().appendRow();
		appendRow.setProduct(getDbContext().load(Product.class,
				new IdImpl("(28,2,0)")));
		appendRow.setUnitQty(1);
		// commits editor
		try {
			newQuotation.commit();
		}
		catch (DBRuntimeException e) {
			newQuotation.abort();
			throw new TransactionCompletionException(e);
		}
		// returns the new customer contact
		return newQuotation;
	}

}

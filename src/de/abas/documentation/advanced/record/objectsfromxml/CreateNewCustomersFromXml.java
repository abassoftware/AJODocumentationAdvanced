package de.abas.documentation.advanced.record.objectsfromxml;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.customer.CustomerEditor;

public class CreateNewCustomersFromXml extends AbstractAjoAccess {

	public static void main(String[] args) {
		new CreateNewCustomersFromXml().runClientProgram(args);
	}

	DbContext dbContext = getDbContext();
	String xmlFile = "files/NewCustomers.xml";
	CustomerEditor customerEditor;

	@Override
	public int run(String[] args) {
		setFileName(args);
		try {
			final SAXBuilder saxBuilder = new SAXBuilder();
			final Document document = saxBuilder.build(xmlFile);
			final Element rootElement = validateAbasXml(document);
			final Element recordSet = rootElement.getChild("RecordSet");
			final List<Element> records = recordSet.getChildren();
			for (final Element record : records) {
				createCustomers(record);
			}
		} catch (final Exception e) {
			dbContext.out().println(e.getMessage());
		} finally {
			if (customerEditor != null) {
				if (customerEditor.active()) {
					customerEditor.abort();
				}
			}
		}
		return 0;
	}

	private void createCustomers(final Element record) {
		final Element head = record.getChild("Head");
		customerEditor = dbContext.newObject(CustomerEditor.class);
		setFields(head);
		// customerEditor.abort();
		customerEditor.commit();
		dbContext.out().println(customerEditor.objectId().getIdno() + " - " + customerEditor.objectId().getSwd());
	}

	private void setFields(final Element head) {
		final List<Element> fields = head.getChildren();
		for (final Element field : fields) {
			final String fieldName = field.getAttributeValue("name");
			final String fieldValue = field.getValue();
			dbContext.out().println(fieldName + " -> " + fieldValue);
			customerEditor.setString(fieldName, fieldValue.trim());
		}
	}

	private void setFileName(String[] args) {
		if (args.length >= 2) {
			xmlFile = args[1];
		}
	}

	private Element validateAbasXml(final Document document) throws Exception {
		final Element rootElement = document.getRootElement();
		if (!rootElement.getName().equals("ABASData")) {
			throw new Exception("xml file: no abas data format");
		}
		return rootElement;
	}

}

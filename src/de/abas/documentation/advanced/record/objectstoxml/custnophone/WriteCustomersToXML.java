package de.abas.documentation.advanced.record.objectstoxml.custnophone;

import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.Query;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.selection.ExpertSelection;
import de.abas.erp.db.selection.Selection;

public class WriteCustomersToXML {

	/**
	 * Creates the XML-file.
	 *
	 * @param ctx The database context.
	 * @param xmlFile The path and name of the future XML-file.
	 * @throws IOException If an I/O error occurs.
	 */
	public void createXMLFile(DbContext ctx, String xmlFile) throws IOException {
		FileOutputStream fileOutputStream = null;
		try {
			// creates root element ABASData
			Element abasData = new Element("ABASData");
			// creates XML document on the basis of the root element
			Document document = new Document(abasData);
			// selects all customers without phone number
			Query<Customer> query = selectCustomersWithoutPhoneNo(ctx);
			// creates RecordSet element
			Element recordSet = createRecordSetElement(abasData, query);
			// stores information about the customers without phone number in
			// XML file
			for (Customer customer : query) {
				createRecordElements(recordSet, customer);
			}
			// defines the format of the XML file
			Format format = Format.getPrettyFormat();
			format = format.setExpandEmptyElements(true);
			// instantiates XMLOutputter using the previously defined format
			XMLOutputter xmlOutputter = new XMLOutputter(format);
			// instantiates FileOutputStream for the previously defined future
			// name
			// and location of the XML file
			fileOutputStream = new FileOutputStream(xmlFile);
			// creates the XML file
			xmlOutputter.output(document, fileOutputStream);
		}
		catch (IOException e) {
			throw new IOException(e.getMessage());
		}
		finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}

	/**
	 * Creates the Field element, defines its Attributes and places it within
	 * the head element.
	 *
	 * @param customer The current Customer object.
	 * @param head The Head element to place the Field element within.
	 * @param fieldName The name of the field to fill.
	 */
	private void createFieldElement(Customer customer, Element head,
			String fieldName) {
		// creates Field element
		Element field = new Element("Field");
		// adds attributes to Field element
		field.setAttribute("name", fieldName);
		field.setAttribute("abasType", Customer.META.getField(fieldName)
				.getErpType());
		// adds content to the Field element
		field.setText(customer.getString(fieldName));
		// places Field element within Head element
		head.addContent(field);
	}

	/**
	 * Creates Record element.
	 *
	 * @param recordSet The Record element's parent element RecordSet.
	 * @param customer The current customer.
	 */
	private void createRecordElements(Element recordSet, Customer customer) {
		// creates Record element
		Element record = new Element("Record");
		// adds attributes to Record element
		record.setAttribute("id", customer.getIdno());
		// places Record element within RecordSet element
		recordSet.addContent(record);
		// creates Head element
		Element head = new Element("Head");
		// places Head element within Record element
		record.addContent(head);
		// makes all Field elements
		createFieldElement(customer, head, "swd");
		createFieldElement(customer, head, "descr");
		createFieldElement(customer, head, "phoneNo");
	}

	/**
	 * Creates RecordSet element.
	 *
	 * @param abasData The root element ABASData of the XML-file.
	 * @param query The result set of the query to get all customers without
	 * phone number.
	 * @return The RecordSet element.
	 */
	private Element createRecordSetElement(Element abasData,
			Query<Customer> query) {
		Element recordSet = new Element("RecordSet");
		// adds attributes to RecordSet element
		recordSet.setAttribute("action", "update");
		if (!query.execute().isEmpty()) {
			recordSet.setAttribute("database", String.valueOf(query.execute()
					.get(0).getDBNo().getCode()));
			recordSet.setAttribute("group", String.valueOf(query.execute().get(
					0).getGrpNo()));
		}
		// places RecordSet element within ABASData element
		abasData.addContent(recordSet);
		return recordSet;
	}

	/**
	 * Selects all customers without phone number.
	 *
	 * @param ctx The database context.
	 * @return The result set of the query to get all customers without phone
	 * number.
	 */
	private Query<Customer> selectCustomersWithoutPhoneNo(DbContext ctx) {
		String criteria = "phoneNo=`;@englvar=(Yes);@language=en";
		Selection<Customer> selection =
				ExpertSelection.create(Customer.class, criteria);
		Query<Customer> query = ctx.createQuery(selection);
		return query;
	}

}

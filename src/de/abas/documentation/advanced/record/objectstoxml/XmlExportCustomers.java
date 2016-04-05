package de.abas.documentation.advanced.record.objectstoxml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.FieldSet;
import de.abas.erp.db.FieldValueProvider;
import de.abas.erp.db.Query;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;

public class XmlExportCustomers extends AbstractAjoAccess {

	public static void main(String[] args) {
		new XmlExportCustomers().runClientProgram(args);
	}

	final DbContext dbContext = getDbContext();
	String fileName = "XMLExportCustomer.xml";
	final FieldSet<FieldValueProvider> fieldSet = FieldSet.of("idno",
			"swd",
			"descrOperLang",
			"zipCode",
			"town",
			"turnoverFY",
			"DBNo",
			"grpNo");

	@Override
	public int run(String[] args) {
		setFileName(args);
		try (FileOutputStream fos = new FileOutputStream(fileName)) {

			// make tags
			final Element rootElement = new Element("ABASData");
			final Element recordSet = new Element("RecordSet");

			// enter tags in document
			final Document document = new Document(rootElement);
			rootElement.addContent(recordSet);
			recordSet.setAttribute("action", "export");

			final List<Customer> customers = getCustomers("70000", "70010");

			// set database and group as RecordSet attributes
			final String dbNo = Integer.toString(customers.get(0).getDBNo().getCode());
			final String grpNo = Integer.toString(customers.get(0).getGrpNo());
			recordSet.setAttribute("database", dbNo);
			recordSet.setAttribute("group", grpNo);

			for (final Customer customer : customers) {
				final Element record = new Element("Record");
				recordSet.addContent(record);
				final Element head = new Element("Head");
				record.addContent(head);
				makeFieldTags(head, customer);
			}

			// create file
			final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
			xmlOutputter.output(document, fos);
		} catch (final IOException e) {
			dbContext.out().println(e.getMessage());
			return 1;
		}
		return 0;
	}

	private List<Customer> getCustomers(String idnoBegin, String idnoEnd) {
		final Query<Customer> query = dbContext.createQuery(SelectionBuilder.create(Customer.class)
				.add(Conditions.between(Customer.META.idno, idnoBegin, idnoEnd)).build());
		query.setFields(fieldSet);
		return query.execute();
	}

	private void makeFieldTags(final Element head, final Customer customer) {
		final String[] fields = fieldSet.getFields();
		for (final String fieldName : fields) {
			// set other fields as field tags
			if (!(fieldName.equals("DBNo") || fieldName.equals("grpNo"))) {
				final Element field = new Element("Field");
				field.setAttribute("name", fieldName);
				field.setAttribute("abasType", Customer.META.getField(fieldName).getErpType());
				field.setText(customer.getString(fieldName));
				head.addContent(field);
			}
		}
	}

	private void setFileName(String[] args) {
		if (args.length >= 2) {
			fileName = args[1];
		}
	}

}

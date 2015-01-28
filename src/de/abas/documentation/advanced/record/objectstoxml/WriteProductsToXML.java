package de.abas.documentation.advanced.record.objectstoxml;

import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.Query;
import de.abas.erp.db.schema.part.Product;
import de.abas.erp.db.schema.part.Product.Row;
import de.abas.erp.db.selection.SelectionBuilder;

public class WriteProductsToXML extends AbstractAjoAccess {

	public static void main(String[] args) {
		WriteProductsToXML writeProductsToXML = new WriteProductsToXML();
		writeProductsToXML.runClientProgram(args);
	}

	/**
	 * Creates Field element within Head element.
	 *
	 * @param product The current product to get the field value from.
	 * @param head The Head element to place the Field element within.
	 * @param fieldName The name of the field.
	 */
	private void createFieldElement(Product product, Element head, String fieldName) {
		// creates Field element
		Element field = new Element("Field");
		// adds attributes to Field element
		field.setAttribute("name", fieldName);
		field.setAttribute("abasType", Product.META.getField(fieldName).getErpType());
		// adds content to Field element
		field.setText(product.getString(fieldName));
		// places Field element within Head element
		head.addContent(field);
	}

	/**
	 * Creates Field element within Row element.
	 *
	 * @param row The current row of the current product to get the field value
	 * from.
	 * @param rowElement The Row element to place the Field element within.
	 * @param fieldName The name of the field.
	 */
	private void createFieldElement(Row row, Element rowElement, String fieldName) {
		// creates Field element
		Element field = new Element("Field");
		// adds attributes to Field element
		field.setAttribute("name", fieldName);
		field.setAttribute("abasType", Product.Row.META.getField(fieldName)
				.getErpType());
		// adds content to Field element
		field.setText(row.getString(fieldName));
		// places Field element within Row element
		rowElement.addContent(field);
	}

	@Override
	public void run(String[] args) {
		FileOutputStream fileOutputStream = null;
		// database context
		DbContext ctx = getDbContext();
		// future name and location of XML file
		String xmlFile =
				"src/de/abas/documentation/advanced/record/objectstoxml/Products.xml";
		// selects all products
		SelectionBuilder<Product> selection = SelectionBuilder.create(Product.class);
		Query<Product> query = ctx.createQuery(selection.build());
		try {
			// creates root element ABASData
			Element abasData = new Element("ABASData");
			// creates XML document on the basis of the root element
			Document document = new Document(abasData);
			// creates RecordSet element
			Element recordSet = new Element("RecordSet");
			// adds attributes to RecordSet element
			recordSet.setAttribute("action", "update");
			recordSet.setAttribute("database",
					String.valueOf(query.execute().get(0).getDBNo().getCode()));
			recordSet.setAttribute("group",
					String.valueOf(query.execute().get(0).getGrpNo()));
			// places RecordSet element within ABASData element
			abasData.addContent(recordSet);
			for (Product product : query) {
				// creates new Record element
				Element record = new Element("Record");
				// places Record element within RecordSet element
				recordSet.addContent(record);
				// creates Head element
				Element head = new Element("Head");
				// places Head element within Record element
				record.addContent(head);
				// creates all Field elements
				createFieldElement(product, head, "idno");
				createFieldElement(product, head, "swd");
				createFieldElement(product, head, "descr");
				createFieldElement(product, head, "stock");
				createFieldElement(product, head, "salesPrice");
				// if there are table rows
				if (product.table().getRowCount() != 0) {
					Iterable<Row> rows = product.table().getRows();
					for (Row row : rows) {
						// creates Row element
						Element rowElement = new Element("Row");
						// adds attribute to Row element
						rowElement.setAttribute("number",
								String.valueOf(row.getRowNo()));
						// places Row element within Record element
						record.addContent(rowElement);
						// creates all Field elements
						createFieldElement(row, rowElement, "prodListElem");
						createFieldElement(row, rowElement, "elemDescr");
					}
				}
			}
			// instantiates XMLOutputter using the pretty formatting
			XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
			// instantiates FileOutputStream for the previously defined future
			// name
			// and location of the XML file
			fileOutputStream = new FileOutputStream(xmlFile);
			// creates the XML file
			xmlOutputter.output(document, fileOutputStream);
			// displays success message
			ctx.out().println("XML file was created!");
		}
		catch (IOException e) {
			ctx.out().println("An IO exception occurred: " + e.getMessage());
		}
		finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			}
			catch (IOException e) {
				ctx.out().println("An IO exception occurred: " + e.getMessage());
			}
		}
	}

}

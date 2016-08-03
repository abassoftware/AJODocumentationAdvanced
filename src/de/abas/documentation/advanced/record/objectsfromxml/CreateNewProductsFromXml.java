package de.abas.documentation.advanced.record.objectsfromxml;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.FieldValueSetter;
import de.abas.erp.db.schema.part.ProductEditor;
import de.abas.erp.db.schema.part.ProductEditor.Row;

public class CreateNewProductsFromXml extends AbstractAjoAccess {

	public static void main(String[] args) {
		new CreateNewProductsFromXml().runClientProgram(args);
	}

	DbContext dbContext = getDbContext();
	String xmlFile = "files/NewProducts.xml";
	ProductEditor productEditor;

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
				createProducts(record);
			}
		} catch (final Exception e) {
			dbContext.out().println(e.getMessage());
		} finally {
			if (productEditor != null) {
				if (productEditor.active()) {
					productEditor.abort();
				}
			}
		}
		return 0;
	}

	private void createProducts(final Element record) {
		final Element head = record.getChild("Head");
		productEditor = dbContext.newObject(ProductEditor.class);
		setFields(head, productEditor);
		final List<Element> rows = record.getChildren("Row");
		for (final Element row : rows) {
			final Row productEditorRow = productEditor.table().appendRow();
			setFields(row, productEditorRow);
		}
		// productEditor.abort();
		productEditor.commit();
		dbContext.out().println(productEditor.objectId().getIdno() + " - " + productEditor.objectId().getSwd());
	}

	private void setFields(final Element parent, FieldValueSetter headOrRow) {
		final List<Element> fields = parent.getChildren();
		for (final Element field : fields) {
			final String fieldName = field.getAttributeValue("name");
			final String fieldValue = field.getValue();
			dbContext.out().println(fieldName + " -> " + fieldValue);
			headOrRow.setString(fieldName, fieldValue.trim());
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

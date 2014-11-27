package de.abas.documentation.advanced.record.objectsfromxml;

import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.field.editable.EditableFieldMeta;
import de.abas.erp.db.schema.part.ProductEditor;
import de.abas.erp.db.schema.part.ProductEditor.Row;

public class CreateNewProductsFromXML extends AbstractAjoAccess {

	public static void main(String[] args) {
		CreateNewProductsFromXML createNewProductsFromXML =
				new CreateNewProductsFromXML();
		createNewProductsFromXML.runClientProgram(args);
	}

	@Override
	public void run(String[] args) {
		ProductEditor productEditor = null;
		// path of XML file
		String xmlFile = "C:/Users/abas/Documents/New Products.xml";
		try {
			// instantiates SAXBuilder
			SAXBuilder saxBuilder = new SAXBuilder();
			// parses the xml file
			Document document = saxBuilder.build(xmlFile);
			// the <ABASData> tag
			Element rootElement = document.getRootElement();
			// the <RecordSet> tag
			List<Element> recordSet = rootElement.getChildren();
			// the <Record> tags
			List<Element> records = recordSet.get(0).getChildren();
			for (Element record : records) {
				productEditor = getDbContext().newObject(ProductEditor.class);
				// the <Head> tag
				Element head = record.getChild("Head");
				// the <Field> tags
				List<Element> fields = head.getChildren();
				for (Element field : fields) {
					// sets head fields for new product
					EditableFieldMeta fieldMeta =
							productEditor.getFieldMeta(field
									.getAttributeValue("name"));
					fieldMeta.setValue(field.getValue());
				}
				// the <Row> tags
				List<Element> rows = record.getChildren("Row");
				for (Element row : rows) {
					Row appendRow = productEditor.table().appendRow();
					// the <Field> tags of the rows
					List<Element> rowFields = row.getChildren();
					for (Element rowField : rowFields) {
						// set row fields for new product
						EditableFieldMeta fieldMeta =
								appendRow.getFieldMeta(rowField
										.getAttributeValue("name"));
						fieldMeta.setValue(rowField.getValue());
					}
				}
				// saves the new product
				productEditor.commit();
				// displays success message
				getDbContext().out().println(
						"The following customer was successfully created: "
								+ productEditor.objectId());
			}
		}
		catch (JDOMException e) {
			getDbContext().out().println(e.getMessage());
		}
		catch (IOException e) {
			getDbContext().out().println(e.getMessage());
		}
		finally {
			if (productEditor != null) {
				if (productEditor.active()) {
					productEditor.abort();
				}
			}
		}
	}

}

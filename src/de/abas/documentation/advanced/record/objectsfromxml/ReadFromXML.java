package de.abas.documentation.advanced.record.objectsfromxml;

import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;

public class ReadFromXML extends AbstractAjoAccess {

	public static void main(String[] args) {
		ReadFromXML readFromXML = new ReadFromXML();
		readFromXML.runClientProgram(args);
	}

	@Override
	public void run(String[] args) {
		DbContext context = getDbContext();
		// path of XML file
		String xmlFile =
				"C:/Users/jasc/Documents/ABAS/AJO/Schulungsunterlage/example.xml";
		try {
			// instantiates SAXBuilder
			SAXBuilder saxBuilder = new SAXBuilder();
			// parses the xml file
			Document document = saxBuilder.build(xmlFile);
			// gets the root-element from the document
			Element rootElement = document.getRootElement();
			context.out().println("Root-element: " + rootElement.getName());
			// gets the RecordSet-tag from the root element
			List<Element> recordSet = rootElement.getChildren();
			context.out().println("RecordSet: " + recordSet.get(0).getName());
			// gets the action-attribute from the RecordSet-tag and prints its
			// name
			// and value
			Attribute action = recordSet.get(0).getAttribute("action");
			context.out().println(" Attribute name: " + action.getName());
			context.out().println("  Attribute value: " + action.getValue());
			// gets the Record-tag from the RecordSet-tag
			List<Element> records = recordSet.get(0).getChildren();
			for (Element record : records) {
				// prints every Record-tag name
				context.out().println("Record: " + record.getName());
				// gets and prints every Head-tag
				Element head = record.getChild("Head");
				context.out().println("Head: " + head.getName());
				// gets the Field-tags from the Head-tag
				List<Element> fields = head.getChildren();
				for (Element field : fields) {
					// prints every Field-tag name
					context.out().println("Field: " + field.getName());
					// gets and prints the Field-tag's attribute name and its
					// value
					Attribute name = field.getAttribute("name");
					context.out().println(" Attribute name: " + name.getName());
					context.out().println(
							"  Attribute value: " + name.getValue());
					// gets and prints the Field-tag's value
					context.out().println("   Tag value: " + field.getValue());
				}
			}
		}
		catch (JDOMException e) {
			context.out().println(e.getMessage());
		}
		catch (IOException e) {
			context.out().println(e.getMessage());
		}
	}

}

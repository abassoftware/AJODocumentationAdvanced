package de.abas.documentation.advanced.record.objectstoxml;

import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import de.abas.documentation.advanced.common.AbstractAjoAccess;

public class XMLExport extends AbstractAjoAccess {

	public static void main(String[] args) {
		XMLExport xmlExport = new XMLExport();
		xmlExport.runClientProgram(args);
	}

	@Override
	public void run(String[] args) {
		try {
			String xmlFile = "C:/Users/abas/Documents/XMLExport.xml";
			// creates root element ABASData
			Element abasData = new Element("ABASData");
			// creates XML document on the basis of the root element
			Document document = new Document(abasData);
			// creates RecordSet element
			Element recordSet = new Element("RecordSet");
			// places RecordSet element within ABASData element
			abasData.addContent(recordSet);
			// assign attributes to RecordSet element
			recordSet.setAttribute("action", "new");
			recordSet.setAttribute("database", "0");
			recordSet.setAttribute("group", "1");
			// creates Record element
			Element record = new Element("Record");
			// places Record element within RecordSet element
			recordSet.addContent(record);
			// creates Head element
			Element head = new Element("Head");
			// places Head element within Record element
			record.addContent(head);
			createFieldElements(head, "swd", "NETSTARS");
			// instantiates XMLOutputter using the previously defined format
			XMLOutputter xmlOutputter = new XMLOutputter();
			// instantiates FileOutputStream for the previously defined future
			// name
			// and location of the XML file
			FileOutputStream fileOutputStream = new FileOutputStream(xmlFile);
			// creates the XML file
			xmlOutputter.output(document, fileOutputStream);
		}
		catch (IOException e) {
			getDbContext().out().println(
					"An IOException occurred: " + e.getMessage());
		}
	}

	/**
	 * Creates Field elements inside Head element.
	 * 
	 * @param head The Head element to place the Field element in.
	 * @param fieldName The name of the field.
	 * @param fieldContent The content of the field.
	 */
	protected void createFieldElements(Element head, String fieldName,
			String fieldContent) {
		// creates Field element
		Element field = new Element("Field");
		// places Field element within Head element
		head.addContent(field);
		// assign attributes to Field element
		field.setAttribute("name", fieldName);
		// assign content to Field element
		field.setText(fieldContent);
	}

}

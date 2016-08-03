package de.abas.documentation.advanced.record.objectstoxml;

import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;

public class XmlExportBasic extends AbstractAjoAccess {

	public static void main(String[] args) {
		new XmlExportBasic().runClientProgram(args);
	}

	private final DbContext dbContext = getDbContext();
	private String fileName = "XMLExportBasic.xml";

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

			// continue here ---->

			// create file
			final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
			xmlOutputter.output(document, fos);
		} catch (final IOException e) {
			dbContext.out().println(e.getMessage());
			return 1;
		}
		return 0;
	}

	private void setFileName(String[] args) {
		if (args.length >= 2) {
			fileName = args[1];
		}
	}
}
package de.abas.documentation.advanced.record.objectstoxml.custnophone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class AddPhoneNumbers {
	/**
	 * Gets the phone numbers from TXT-file and writes them into the according
	 * Field element of the XML-file.
	 *
	 * @param xmlFile The XML-file.
	 * @param phoneNumbersTXT The TXT-file.
	 * @return Whether or not the process was successful.
	 * @throws IOException If an I/O error occurs.
	 * @throws JDOMException If a JDOM error occurs while changing the XML-file.
	 * @throws AddPhoneNumberException If an error occurs getting the phone
	 * numbers from the TXT-file.
	 */
	public void addPhoneNumbersToXML(String xmlFile, String phoneNumbersTXT)
			throws IOException, JDOMException, AddPhoneNumberException {
		FileOutputStream fileOutputStream = null;
		// gets the phone numbers from the TXT-file and stores them in an
		// ArrayList
		List<String> allPhoneNumbers = getPhoneNumbers(phoneNumbersTXT);
		// adds the phone numbers to the XML-file
		Document document = changeXML(xmlFile, allPhoneNumbers);
		// instantiates XMLOutputter using the previously defined format
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		// instantiates FileOutputStream for the previously defined future name
		// and location of the XML file
		fileOutputStream = new FileOutputStream(xmlFile);
		// creates the XML file
		xmlOutputter.output(document, fileOutputStream);
	}

	/**
	 * Adds the phone numbers to the XML-file.
	 *
	 * @param xmlFile The XML-file.
	 * @param allPhoneNumbers The phone numbers.
	 * @return The changed document.
	 * @throws JDOMException When errors occur while parsing.
	 * @throws IOException When an I/O exception occurs.
	 */
	private Document changeXML(String xmlFile, List<String> allPhoneNumbers)
			throws JDOMException, IOException {
		// initializes count
		int count = 0;
		// uses SAXBuilder to parse the file as XML
		SAXBuilder saxBuilder = new SAXBuilder();
		Document document = saxBuilder.build(new File(xmlFile));
		// gets root element of XML-file
		Element abasData = document.getRootElement();
		// gets RecordSet element
		Element recordSet = abasData.getChild("RecordSet");
		// gets all Record elements
		List<Element> records = recordSet.getChildren();
		// iterates over all Record elements
		for (Element record : records) {
			// gets each Record element's Head element
			Element head = record.getChild("Head");
			// gets all Field elements within Head element
			List<Element> fields = head.getChildren();
			// iterates over all Field elements
			for (Element field : fields) {
				// fills Field element for phoneNumber
				if (field.getAttribute("name").getValue().equals("phoneNo")) {
					try {
						field.setText(allPhoneNumbers.get(count));
					}
					catch (IndexOutOfBoundsException e) {
						field.setText("");
					}
				}
			}
			count++;
		}
		return document;
	}

	/**
	 * Gets the phone numbers from the TXT-file and stores them in an ArrayList.
	 *
	 * @param phoneNumbers The TXT-file.
	 * @return The phone numbers as ArrayList.
	 * @throws AddPhoneNumberException When errors occur.
	 */
	private List<String> getPhoneNumbers(String phoneNumbers)
			throws AddPhoneNumberException {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(phoneNumbers));
			String line = "";
			ArrayList<String> allPhoneNumbers = new ArrayList<String>();
			// consecutively reads lines of text file until end of file reached
			while ((line = bufferedReader.readLine()) != null) {
				allPhoneNumbers.add(line);
			}
			bufferedReader.close();
			return allPhoneNumbers;
		}
		catch (FileNotFoundException e) {
			throw new AddPhoneNumberException(e.getMessage());
		}
		catch (IOException e) {
			throw new AddPhoneNumberException(e.getMessage());
		}
		finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
			catch (IOException e) {
				throw new AddPhoneNumberException(e.getMessage());
			}
		}
	}
}

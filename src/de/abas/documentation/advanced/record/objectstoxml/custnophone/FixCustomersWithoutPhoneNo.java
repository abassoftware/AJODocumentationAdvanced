package de.abas.documentation.advanced.record.objectstoxml.custnophone;

import java.io.IOException;

import org.jdom2.JDOMException;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;

public class FixCustomersWithoutPhoneNo extends AbstractAjoAccess {

	public static void main(String[] args) {
		FixCustomersWithoutPhoneNo fixCustomersWithoutPhoneNo =
				new FixCustomersWithoutPhoneNo();
		fixCustomersWithoutPhoneNo.runClientProgram(args);
	}

	@Override
	public void run(String[] args) {
		// database context
		DbContext ctx = getDbContext();
		String xmlFile = getXMLFile();
		String phoneNumbersTXT = getTXTFile();
		String error = "";
		try {
			new WriteCustomersToXML().createXMLFile(ctx, xmlFile);
			// displays success message
			ctx.out().println("XML file was created!");
			new AddPhoneNumbers()
					.addPhoneNumbersToXML(xmlFile, phoneNumbersTXT);
			// displays success message
			ctx.out().println("Phone numbers successfully added!");
			if (isClientMode()) {
				// change parameters according to your needs:
				// importXMLInClientMode(host, user, password, useRoot, xmlFile,
				// pathToClient, clientPwd, useENGLVAR)
				// host: Server to connect to.
				// user: User to authenticate with. Either abas ERP user or
				// s3user.
				// password: Password for authentication. Either root password
				// in
				// case useRoot is true or s3user's password.
				// useRoot: Whether or not to use root to connect to server and
				// the
				// re-authenticate.
				// xmlFile: Path and Name of XML file.
				// pathToClient: Path to $MANDANTDIR of client.
				// clientPwd: Password for abas ERP client.
				// useENGLVAR: Whether or not English variable names are used
				// within
				// XML file.
				error =
						new ImportXML()
				.importXMLInClientMode(
						"hades",
						"jasc",
						"secure",
						true,
						"/u1/jasc/erp/win/tmp/CustomersWithoutPhoneNo.xml",
						"/u1/jasc/erp", "sy", true);
			}
			else if (isServerMode()) {
				new ImportXML().importXMLInServerMode(xmlFile, true);
			}
			// displays success message
			if (error.equals("")) {
				ctx.out().println("XML import successfully completed.");
			}
			else {
				ctx.out().println(
						"XML import finished without an exception.\n"
								+ "However, there were errors and/or warnings "
								+ "outputted on the terminal:\n" + error);
			}
		}
		catch (IOException e) {
			// displays error message in case of an exception
			ctx.out().println("An IO-Exception occurred: " + e.getMessage());
		}
		catch (JDOMException e) {
			ctx.out().println("A JDOM-Exception occurred: " + e.getMessage());
		}
		catch (AddPhoneNumberException e) {
			ctx.out().println(
					"An AddPhoneNumber-Exception occurred: " + e.getMessage());
		}
	}

	/**
	 * Gets name and location of TXT file to get phone numbers from according to
	 * whether the application runs in server or client mode.
	 *
	 * @return The name and location of the TXT file
	 */
	private String getTXTFile() {
		// future name and location of XML file
		String phoneNumbersTXT = "";
		if (isClientMode()) {
			phoneNumbersTXT = "//client path to tmp/PhoneNumbers.txt";
		}
		else if (isServerMode()) {
			phoneNumbersTXT = "win/tmp/PhoneNumbers.txt";
		}
		return phoneNumbersTXT;
	}

	/**
	 * Gets name and location of XML file according to whether the application
	 * runs in server or client mode.
	 *
	 * @return The name and location of the XML file as String.
	 */
	private String getXMLFile() {
		// future name and location of XML file
		String xmlFile = "";
		if (isClientMode()) {
			xmlFile = "//client path to tmp/CustomersWithoutPhoneNo.xml";
		}
		else if (isServerMode()) {
			xmlFile = "win/tmp/CustomersWithoutPhoneNo.xml";
		}
		return xmlFile;
	}

	/**
	 * Checks whether the application is running in server mode.
	 *
	 * @return True is server mode.
	 */
	private boolean isClientMode() {
		return getMode().equals(ContextMode.CLIENT_MODE.toString());
	}

	/**
	 * Checks whether the application is running in client mode.
	 *
	 * @return True if client mode.
	 */
	private boolean isServerMode() {
		return getMode().equals(ContextMode.SERVER_MODE.toString());
	}

}

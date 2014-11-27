package de.abas.documentation.advanced.record.objectstoxml.custnophone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class ImportXML {

	/**
	 * Connects to server either by root and re-authentication to other user or
	 * by s3user and executes xmlimport.sh. Then imports specified XML file
	 * using specified variable language.
	 *
	 * @param host Server to connect to.
	 * @param user User to authenticate with. Either abas ERP user or s3user.
	 * @param password Password for authentication. Either root password in case
	 * useRoot is true or s3user's password.
	 * @param useRoot Whether or not to use root to connect to server and the
	 * re-authenticate
	 * @param xmlFile Path and Name of XML file.
	 * @param pathToClient Path to $MANDANTDIR of client.
	 * @param clientPwd Password for abas ERP client.
	 * @param useENGLVAR Whether or not English variable names are used within
	 * XML file.
	 * @return Any console error messages.
	 * @throws IOException Thrown if any errors occur.
	 */
	public String importXMLInClientMode(String host, String user,
			String password, boolean useRoot, String xmlFile,
			String pathToClient, String clientPwd, boolean useENGLVAR)
			throws IOException {
		Session session = null;
		try {
			String output = "";
			String changeUser = "";
			if (useRoot) {
				// uses root to authenticate, then switches user, goes to
				// $MANDANDIR
				// and gets user environment
				// this is necessary if user does not have password to
				// authenticate
				// on server
				// user must be abas ERP user
				session = establishSession(host, "root", password);
				changeUser =
						"su - " + user + ";cd " + pathToClient
								+ ";eval $(sh ./denv.sh);";
			}
			else {
				// authenticates with user only
				// user must be abas ERP user
				session = establishSession(host, user, password);
			}
			// whether or not English variables are used within the XML file
			if (useENGLVAR) {
				session.execCommand(changeUser + "xmlimport.sh -p " + clientPwd
						+ " -o VARLANG=en -I " + xmlFile);
				output = getConsoleErrorOutput(session);
			}
			else {
				session.execCommand(changeUser + "xmlimport.sh -p " + clientPwd
						+ " -I " + xmlFile);
				output = getConsoleErrorOutput(session);
			}
			session.close();
			return output;
		}
		// closes session to free resources then throws IOException again.
		catch (IOException e) {
			if (session != null) {
				session.close();
			}
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Imports specified XML file using specified variable language.
	 *
	 * @param xmlFile Path and Name of XML file.
	 * @param useENGLVAR Whether or not English variable names are used within
	 * XML file.
	 * @throws IOException Thrown if any errors occur.
	 */
	public void importXMLInServerMode(String xmlFile, boolean useENGLVAR)
			throws IOException {
		Runtime runtime = Runtime.getRuntime();
		if (useENGLVAR) {
			runtime.exec("xmlimport.sh -p sy -o VARLANG=en -I " + xmlFile);
		}
		else {
			runtime.exec("xmlimport.sh -p sy -I " + xmlFile);
		}
	}

	/**
	 * Establishes connection to server when in client mode.
	 *
	 * @param host Server to connect to.
	 * @param user User to authenticate with. Either abas ERP user or s3user.
	 * @param password Password for authentication. Either root password in case
	 * useRoot is true or s3user's password.
	 * @return Returns the session.
	 * @throws IOException IOException Thrown if any errors occur.
	 */
	private Session establishSession(String host, String user, String password)
			throws IOException {
		Connection connection = new Connection(host);
		connection.connect();
		connection.authenticateWithPassword(user, password);
		Session session = connection.openSession();
		return session;
	}

	/**
	 * Gets any error output displayed on the terminal console.
	 *
	 * @param session The current session.
	 * @return Any error output.
	 * @throws IOException Thrown if any errors occur.
	 */
	private String getConsoleErrorOutput(Session session) throws IOException {
		String line;
		String output = "";
		InputStream stderr = null;
		BufferedReader bufferedReader = null;
		try {
			// gets error output from session
			stderr = session.getStderr();
			// reads error output line by line
			bufferedReader = new BufferedReader(new InputStreamReader(stderr));
			while ((line = bufferedReader.readLine()) != null) {
				output = output + line + "\n";
			}
			// closes InputStream and BufferedReader to free resources
			stderr.close();
			bufferedReader.close();
			return output;
		}
		// closes InputStream and BufferedReader to free resources
		// then throws IOException again
		catch (IOException e) {
			if (stderr != null) {
				stderr.close();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			throw new IOException(e.getMessage());
		}
	}
}

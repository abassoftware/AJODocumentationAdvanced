package de.abas.documentation.advanced.record.transaction;

import de.abas.erp.db.exception.DBRuntimeException;

public class TransactionCompletionException extends Exception {
	/**
	 * Generated serial UID
	 */
	private static final long serialVersionUID = 5748742296382217959L;

	/**
	 * Default constructor.
	 */
	public TransactionCompletionException() {
		super();
	}

	/**
	 * Constructor with DBRuntimeException.
	 *
	 * @param e The DBRuntimeException.
	 */
	public TransactionCompletionException(DBRuntimeException e) {
		super("Something went wrong!\n" + e.getMessage() + "\nRolling back...",
				e);
	}
}

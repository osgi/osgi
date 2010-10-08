package org.osgi.util.tr069;

/**
 * Exception which will be thrown if some trouble occurs for mapping.
 * 
 */
public class TR069MappingException extends Exception {

	/**
	 * Construct an exception.
	 * 
	 * @param string
	 *            error message.
	 */
	public TR069MappingException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3712320696439886391L;

}

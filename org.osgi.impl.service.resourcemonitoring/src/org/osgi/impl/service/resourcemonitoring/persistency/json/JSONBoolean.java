
package org.osgi.impl.service.resourcemonitoring.persistency.json;

/**
 * 
 */
public class JSONBoolean extends JSONObject {

	private final boolean	value;

	/**
	 * @param pValue
	 */
	public JSONBoolean(boolean pValue) {
		value = pValue;
	}

	/**
	 * @return the value.
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * @param tokenizer
	 * @return the corresponding JSONBoolean.
	 */
	public static JSONBoolean parseJsonBoolean(JsonTokenizer tokenizer) {
		boolean value = Boolean.getBoolean(tokenizer.getCurrentToken());
		JSONBoolean jsonBoolean = new JSONBoolean(value);
		tokenizer.nextToken();
		return jsonBoolean;
	}

	/**
	 * @param value
	 * @return true if JSON Boolean.
	 */
	public static boolean isJsonBoolean(final String value) {
		String trimmedValue = value.trim();
		trimmedValue = trimmedValue.toLowerCase();
		return ("true".equals(trimmedValue) || "false".equals(trimmedValue));
	}

}

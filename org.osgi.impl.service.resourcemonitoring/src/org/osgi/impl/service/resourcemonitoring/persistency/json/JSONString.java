
package org.osgi.impl.service.resourcemonitoring.persistency.json;

/**
 *
 */
public class JSONString extends JSONObject {

	private final String	value;

	/**
	 * @param pValue
	 */
	public JSONString(String pValue) {
		value = pValue;
	}

	/**
	 * @return the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param tokenizer
	 * @return the corresponding JSONString.
	 */
	public static JSONString parseJsonString(JsonTokenizer tokenizer) {
		// next token should be the string value
		tokenizer.nextToken();
		String token = tokenizer.getCurrentToken();

		JSONString jsonString = new JSONString(token);

		// next token should be a "
		tokenizer.nextToken();
		token = tokenizer.getCurrentToken();
		if (!token.equals("\"")) {
			throw new RuntimeException("Expected a double quote, found "
					+ token);
		}
		tokenizer.nextToken();

		return jsonString;
	}

	/**
	 * return hardcoded true.
	 */
	@Override
	public boolean isJsonString() {
		return true;
	}

}

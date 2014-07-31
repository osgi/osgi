
package org.osgi.impl.service.resourcemanagement.persistency.json;

/**
 *
 */
public class JSONLong extends JSONObject {

	private final long	value;

	/**
	 * @param pValue
	 */
	public JSONLong(long pValue) {
		value = pValue;
	}

	/**
	 * @return the value.
	 */
	public long getValue() {
		return value;
	}

	/**
	 * @param tokenizer
	 * @return the corresponding JSONLong.
	 */
	public static JSONLong parseJsonLong(JsonTokenizer tokenizer) {
		long value = Long.parseLong(tokenizer.getCurrentToken());
		JSONLong jsonLong = new JSONLong(value);
		tokenizer.nextToken();
		return jsonLong;
	}

}

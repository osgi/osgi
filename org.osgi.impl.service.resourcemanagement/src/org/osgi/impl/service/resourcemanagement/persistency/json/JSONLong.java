package org.osgi.impl.service.resourcemanagement.persistency.json;

public class JSONLong extends JSONObject {

	private final long value;

	public JSONLong(long pValue) {
		value = pValue;
	}

	public long getValue() {
		return value;
	}

	public static JSONLong parseJsonLong(JsonTokenizer tokenizer) {

		long value = Long.parseLong(tokenizer.getCurrentToken());
		JSONLong jsonLong = new JSONLong(value);
		tokenizer.nextToken();
		return jsonLong;
	}



}

package org.osgi.impl.service.resourcemanagement.persistency.json;

public class JSONString extends JSONObject {

	private final String value;

	public JSONString(String pValue) {
		value = pValue;
	}

	public String getValue() {
		return value;
	}

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

	@Override
	public boolean isJsonString() {
		return true;
	}


}

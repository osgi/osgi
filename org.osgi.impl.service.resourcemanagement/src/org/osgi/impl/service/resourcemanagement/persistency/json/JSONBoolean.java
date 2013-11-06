package org.osgi.impl.service.resourcemanagement.persistency.json;

public class JSONBoolean extends JSONObject {

	private final boolean value;

	public JSONBoolean(boolean pValue) {
		value = pValue;
	}

	public boolean getValue() {
		return value;
	}

	public static JSONBoolean parseJsonBoolean(JsonTokenizer tokenizer) {
		boolean value = Boolean.parseBoolean(tokenizer.getCurrentToken());
		JSONBoolean jsonBoolean = new JSONBoolean(value);
		tokenizer.nextToken();
		return jsonBoolean;
	}

	public static boolean isJsonBoolean(final String value) {
		String trimmedValue = value.trim();
		trimmedValue = trimmedValue.toLowerCase();
		return ("true".equals(trimmedValue) || "false".equals(trimmedValue));
	}


}

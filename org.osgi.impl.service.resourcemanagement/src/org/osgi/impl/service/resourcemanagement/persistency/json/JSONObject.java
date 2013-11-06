package org.osgi.impl.service.resourcemanagement.persistency.json;

public abstract class JSONObject {

	public static JSONObject parseJsonObject(String jsonAsString) {
		JsonTokenizer tokenizer = new JsonTokenizer(jsonAsString);
		return parseJsonObject(tokenizer);
	}

	public static JSONObject parseJsonObject(JsonTokenizer tokenizer) {
		JSONObject object = null;
		String token = null;

		token = tokenizer.getCurrentToken();

		if (token.equals("[")) {
			object = JSONArray.parseJsonArray(tokenizer);
		} else if (token.equals("{")) {
			object = JSONList.parseJSONList(tokenizer);
		} else if (token.equals("\"")) {
			object = JSONString.parseJsonString(tokenizer);
		} else if (Character.isDigit(token.charAt(0))) {
			object = JSONLong.parseJsonLong(tokenizer);
		} else if (JSONBoolean.isJsonBoolean(token)) {
			object = JSONBoolean.parseJsonBoolean(tokenizer);
		} else {
			throw new RuntimeException("unexpected token : " + token);
		}

		return object;
	}

	public boolean isJsonString() {
		return false;
	}

}

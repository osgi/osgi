
package org.osgi.impl.service.resourcemonitoring.persistency.json;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JSONArray extends JSONObject {

	// List<JSONObject> elements
	private final List<JSONObject> elements;

	/**
	 * Create a new JSONArray
	 */
	public JSONArray() {
		elements = new ArrayList<>();
	}

	/**
	 * @return List<JSONObject> of elements.
	 */
	public List<JSONObject> getElements() {
		return elements;
	}

	/**
	 * @param tokenizer
	 * @return the corresponding JSONArray.
	 */
	public static JSONArray parseJsonArray(JsonTokenizer tokenizer) {
		JSONArray jsonArray = new JSONArray();
		String token = null;
		// parse next token
		tokenizer.nextToken();

		while ((token = tokenizer.getCurrentToken()) != null) {
			if (token.equals("]")) {
				// end of array
				break;
			}

			// parse next json object
			JSONObject object = JSONObject.parseJsonObject(tokenizer);

			// store the parsed object
			jsonArray.getElements().add(object);

			// check next token is a , or ]
			// tokenizer.nextToken();
			token = tokenizer.getCurrentToken();
			if ((!token.equals(",")) && (!token.equals("]"))) {
				throw new RuntimeException("Expected a \",\" or \"]\", found "
						+ token);
			} else
				if (token.equals(",")) {
					// next token
					tokenizer.nextToken();
				}
		}
		// the array is now fully parsed, go to the next token
		tokenizer.nextToken();

		return jsonArray;
	}

}

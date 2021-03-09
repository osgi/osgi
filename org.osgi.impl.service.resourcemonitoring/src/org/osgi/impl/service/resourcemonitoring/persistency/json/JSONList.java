/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.resourcemonitoring.persistency.json;

import java.util.Hashtable;
import java.util.Map;

/**
 *
 */
public class JSONList extends JSONObject {

	/**
	 * Map < String, JSONObject > elements.
	 */
	private final Map<String,JSONObject> elements;

	/**
	 * 
	 */
	public JSONList() {
		elements = new Hashtable<>();
	}

	/**
	 * @return Map < String, JSONObject > of elements.
	 */
	public Map<String,JSONObject> getElements() {
		return elements;
	}

	/**
	 * @param tokenizer
	 * @return the corresponding JSONList.
	 */
	public static JSONList parseJSONList(JsonTokenizer tokenizer) {
		JSONList jsonList = new JSONList();

		String token = null;
		// next token
		tokenizer.nextToken();
		while ((token = tokenizer.getCurrentToken()) != null) {
			if (token.equals("}")) {
				// end of the list
				break;
			}

			// the next object should be a Json string
			JSONString jsonString = JSONString.parseJsonString(tokenizer);

			// next token should be a ":"
			// tokenizer.nextToken();
			token = tokenizer.getCurrentToken();
			if (!token.equals(":")) {
				throw new RuntimeException("Expect a \":\", found " + token);
			}

			// get next token and parse it
			tokenizer.nextToken();
			JSONObject valueJsonObject = JSONObject.parseJsonObject(tokenizer);

			// store value
			jsonList.elements.put(jsonString.getValue(), valueJsonObject);

			// next token could be either a "," or a end of list ("}")
			// tokenizer.nextToken();
			token = tokenizer.getCurrentToken();
			if ((!token.equals(",")) && (!token.equals("}"))) {
				throw new RuntimeException("Expected \",\" or \"}\", found " + token);
			} else
				if (token.equals(",")) {
					tokenizer.nextToken();
				}

		}

		// end of the list, go to the next token
		tokenizer.nextToken();

		return jsonList;
	}

}

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

/**
 *
 */
public abstract class JSONObject {

	/**
	 * @param jsonAsString
	 * @return the corresponding JSONObject.
	 */
	public static JSONObject parseJsonObject(String jsonAsString) {
		JsonTokenizer tokenizer = new JsonTokenizer(jsonAsString);
		return parseJsonObject(tokenizer);
	}

	/**
	 * @param tokenizer
	 * @return the corresponding JSONObject.
	 */
	public static JSONObject parseJsonObject(JsonTokenizer tokenizer) {
		JSONObject object = null;
		String token = null;

		token = tokenizer.getCurrentToken();

		if (token.equals("[")) {
			object = JSONArray.parseJsonArray(tokenizer);
		} else {
			if (token.equals("{")) {
				object = JSONList.parseJSONList(tokenizer);
			} else {
				if (token.equals("\"")) {
					object = JSONString.parseJsonString(tokenizer);
				} else {
					if (Character.isDigit(token.charAt(0))) {
						object = JSONLong.parseJsonLong(tokenizer);
					} else {
						if (JSONBoolean.isJsonBoolean(token)) {
							object = JSONBoolean.parseJsonBoolean(tokenizer);
						} else {
							throw new RuntimeException("unexpected token : " + token);
						}
					}
				}
			}
		}
		return object;
	}

	/**
	 * @return hardcoded false.
	 */
	public boolean isJsonString() {
		return false;
	}

}

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

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
public class JSONBoolean extends JSONObject {

	private final boolean	value;

	/**
	 * @param pValue
	 */
	public JSONBoolean(boolean pValue) {
		value = pValue;
	}

	/**
	 * @return the value.
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * @param tokenizer
	 * @return the corresponding JSONBoolean.
	 */
	public static JSONBoolean parseJsonBoolean(JsonTokenizer tokenizer) {
		boolean value = Boolean.getBoolean(tokenizer.getCurrentToken());
		JSONBoolean jsonBoolean = new JSONBoolean(value);
		tokenizer.nextToken();
		return jsonBoolean;
	}

	/**
	 * @param value
	 * @return true if JSON Boolean.
	 */
	public static boolean isJsonBoolean(final String value) {
		String trimmedValue = value.trim();
		trimmedValue = trimmedValue.toLowerCase();
		return ("true".equals(trimmedValue) || "false".equals(trimmedValue));
	}

}

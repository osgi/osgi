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
 */
public class JsonTokenizer {

	private final String	stringToBeParsed;
	private String			currentToken;
	private int				currentPosition	= 0;

	/**
	 * @param pStringToBeParsed
	 */
	public JsonTokenizer(String pStringToBeParsed) {
		stringToBeParsed = pStringToBeParsed;
		nextToken();
	}

	/**
	 * Go further into the string to parse the next token. This new token is
	 * retrieved throught a call to current token;
	 */
	public void nextToken() {
		// String token = null;
		int initialPosition = currentPosition;
		while ((currentPosition < stringToBeParsed.length())
				&& (!isDelimeter())) {
			currentPosition++;
		}

		if (currentPosition == stringToBeParsed.length()) {
			currentToken = null;
			return;
		}

		int endPosition = currentPosition;
		if (endPosition - initialPosition == 0) {
			endPosition++;
			currentPosition++;
		}
		currentToken = stringToBeParsed.substring(initialPosition, endPosition);

	}

	/**
	 * Get current token.
	 * 
	 * @return current token.
	 */
	public String getCurrentToken() {
		return currentToken;
	}

	/**
	 * Is the current character a JSON delimiter. A JSON delimiter is one of the
	 * following characters:
	 * <ul>
	 * <li>[</li>
	 * <li>]</li>
	 * <li>{</li>
	 * <li></li>
	 * <li>,</li>
	 * <li>:</li>
	 * <li>"</li>
	 * </ul>
	 * 
	 * @return true if the current character is a json delimiter.
	 */
	private boolean isDelimeter() {
		char currentCharacter = stringToBeParsed.charAt(currentPosition);
		return (currentCharacter == '[') || (currentCharacter == ']')
				|| (currentCharacter == '{') || (currentCharacter == '}')
				|| (currentCharacter == ',') || (currentCharacter == ':')
				|| (currentCharacter == '"');
	}
}

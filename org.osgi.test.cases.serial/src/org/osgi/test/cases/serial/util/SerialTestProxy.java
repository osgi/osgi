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

package org.osgi.test.cases.serial.util;

import java.util.StringTokenizer;
import org.osgi.test.support.step.TestStepProxy;

public class SerialTestProxy {
	private static final String	SEPARATOR_COMMA	= ",";
	private static final String	SPACE			= " ";
	private static final String	EMPTY			= "";
	private TestStepProxy		testStepProxy;

	public SerialTestProxy(TestStepProxy testStepProxy) {
		this.testStepProxy = testStepProxy;
	}
	
	public void close(){
		this.testStepProxy.close();
	}

	public String[] executeTestStep(String command, String message, String[] parameters) {
		String stepId = command;
		String userPrompt = message + SEPARATOR_COMMA + toString(parameters);
		String result = testStepProxy.execute(stepId, userPrompt);
		return toStringArray(result);
	}

	private String[] toStringArray(String str) {
		if (str == null || str.length() == 0) {
			return new String[0];
		}
		StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COMMA);
		String[] stringArray = new String[tokenizer.countTokens()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = tokenizer.nextToken();
			if (SPACE.equals(stringArray[i])) {
				stringArray[i] = null;
			}
		}
		return stringArray;
	}

	private String toString(String[] stringArray) {
		if (stringArray == null || stringArray.length == 0) {
			return new String();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < stringArray.length; i++) {
			if (stringArray[i] == null || EMPTY.equals(stringArray[i])) {
				buf.append(SPACE);
			} else {
				buf.append(stringArray[i]);
			}
			buf.append(SEPARATOR_COMMA);
		}
		return buf.toString();
	}
}

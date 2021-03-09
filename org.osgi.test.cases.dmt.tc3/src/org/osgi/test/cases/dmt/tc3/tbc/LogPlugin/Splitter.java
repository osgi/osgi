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
package org.osgi.test.cases.dmt.tc3.tbc.LogPlugin;

import java.util.Vector;

class Splitter {
	static String[] split(String input, char sep, int limit) {
		Vector<String> v = new Vector<>();
		boolean limited = (limit > 0);
		int applied = 0;
		int index = 0;
		StringBuffer part = new StringBuffer();
		while (index < input.length()) {
			char ch = input.charAt(index);
			if (ch != sep)
				part.append(ch);
			else {
				++applied;
				v.add(part.toString());
				part = new StringBuffer();
			}
			++index;
			if (limited && applied == limit - 1)
				break;
		}
		while (index < input.length()) {
			char ch = input.charAt(index);
			part.append(ch);
			++index;
		}
		v.add(part.toString());
		int last = v.size();
		if (0 == limit) {
			for (int j = v.size() - 1; j >= 0; --j) {
				String s = v.elementAt(j);
				if ("".equals(s))
					--last;
				else
					break;
			}
		}
		String[] ret = new String[last];
		for (int i = 0; i < last; ++i)
			ret[i] = v.elementAt(i);
		return ret;
	}
}

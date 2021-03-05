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

package org.osgi.test.support.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Strings {
	private final static Pattern LIST_SPLITTER_PATTERN = Pattern
			.compile("\\s*,\\s*");

	public static List<String> split(String s) {
		if (s == null || (s = s.trim()).isEmpty())
			return new ArrayList<>();
		return toList(LIST_SPLITTER_PATTERN.split(s, 0));
	}

	public static List<String> split(String regex, String s) {
		if (s == null || (s = s.trim()).isEmpty())
			return new ArrayList<>();
		return toList(s.split(regex, 0));
	}

	@SafeVarargs
	private static <T> List<T> toList(T... array) {
		List<T> list = new ArrayList<>(array.length);
		Collections.addAll(list, array);
		return list;
	}
}

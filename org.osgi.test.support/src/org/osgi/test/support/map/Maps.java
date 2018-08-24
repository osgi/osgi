/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.support.map;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Maps {

	public static <K, V> Map<K,V> mapOf() {
		return Collections.emptyMap();
	}

	public static <K, V> Map<K,V> mapOf(K k1, V v1) {
		Map<K,V> map = new LinkedHashMap<>();
		map.put(k1, v1);
		return Collections.unmodifiableMap(map);
	}

	public static <K, V> Map<K,V> map​Of(K k1, V v1, K k2, V v2) {
		Map<K,V> map = new LinkedHashMap<>();
		map.put(k1, v1);
		map.put(k2, v2);
		return Collections.unmodifiableMap(map);
	}

	public static <K, V> Map<K,V> map​Of(K k1, V v1, K k2, V v2, K k3, V v3) {
		Map<K,V> map = new LinkedHashMap<>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		return Collections.unmodifiableMap(map);
	}

	public static <K, V> Map<K,V> map​Of(K k1, V v1, K k2, V v2, K k3, V v3,
			K k4, V v4) {
		Map<K,V> map = new LinkedHashMap<>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		map.put(k4, v4);
		return Collections.unmodifiableMap(map);
	}
}

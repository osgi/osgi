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

package org.osgi.test.support.dictionary;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DictionaryMap<K, V> extends AbstractMap<K,V> {

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K,V> asMap(Dictionary<K,V> dict) {
		if (dict instanceof Map) {
			return (Map<K,V>) dict;
		}
		return new DictionaryMap<>(dict);
	}

	private final Dictionary<K,V> dict;

	private DictionaryMap(Dictionary<K,V> dict) {
		this.dict = dict;
	}

	Enumeration<K> keys() {
		return dict.keys();
	}

	@Override
	public int size() {
		return dict.size();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null) {
			return false;
		}
		return dict.get(key) != null;
	}

	@Override
	public V get(Object key) {
		if (key == null) {
			return null;
		}
		return dict.get(key);
	}

	@Override
	public V put(K key, V value) {
		return dict.put(key, value);
	}

	@Override
	public V remove(Object key) {
		if (key == null) {
			return null;
		}
		return dict.remove(key);
	}

	@Override
	public void clear() {
		Enumeration<K> enumerator = dict.keys();
		while (enumerator.hasMoreElements()) {
			dict.remove(enumerator.nextElement());
		}
	}

	@Override
	public Set<Map.Entry<K,V>> entrySet() {
		return new EntrySet();
	}

	final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
		@Override
		public Iterator<Map.Entry<K,V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return DictionaryMap.this.size();
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Map.Entry) {
				Map.Entry< ? , ? > e = (Map.Entry< ? , ? >) o;
				return containsEntry(e);
			}
			return false;
		}

		private boolean containsEntry(Map.Entry< ? , ? > e) {
			Object key = e.getKey();
			if (key == null) {
				return false;
			}
			Object value = e.getValue();
			if (value == null) {
				return false;
			}
			return Objects.equals(DictionaryMap.this.get(key), value);
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Map.Entry) {
				Map.Entry< ? , ? > e = (Map.Entry< ? , ? >) o;
				if (containsEntry(e)) {
					DictionaryMap.this.remove(e.getKey());
					return true;
				}
			}
			return false;
		}

		@Override
		public void clear() {
			DictionaryMap.this.clear();
		}
	}

	final class EntryIterator implements Iterator<Map.Entry<K,V>> {
		private final Enumeration<K>	enumerator	= DictionaryMap.this.keys();
		private K						key			= null;

		@Override
		public boolean hasNext() {
			return enumerator.hasMoreElements();
		}

		@Override
		public Map.Entry<K,V> next() {
			key = enumerator.nextElement();
			return new Entry(key, DictionaryMap.this.get(key));
		}

		@Override
		public void remove() {
			if (key == null) {
				throw new IllegalStateException();
			}
			DictionaryMap.this.remove(key);
			key = null;
		}
	}

	final class Entry extends SimpleEntry<K,V> {
		private static final long serialVersionUID = 1L;

		Entry(K key, V value) {
			super(key, value);
		}

		@Override
		public V setValue(V value) {
			DictionaryMap.this.put(getKey(), value);
			return super.setValue(value);
		}
	}
}
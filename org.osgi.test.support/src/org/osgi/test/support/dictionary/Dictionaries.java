/*
 * Copyright (c) OSGi Alliance (2018, 2019). All Rights Reserved.
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

import static java.util.Objects.requireNonNull;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Dictionaries {

	private Dictionaries() {}

	/**
	 * Return a Map wrapper around a Dictionary.
	 *
	 * @param <K> The type of the key.
	 * @param <V> The type of the value.
	 * @param dictionary The dictionary to wrap.
	 * @return A Map object which wraps the specified dictionary. If the
	 *         specified dictionary can be cast to a Map, then the specified
	 *         dictionary is returned.
	 */
	public static <K, V> Map<K,V> asMap(
			Dictionary< ? extends K, ? extends V> dictionary) {
		if (dictionary instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<K,V> coerced = (Map<K,V>) dictionary;
			return coerced;
		}
		return new DictionaryAsMap<>(dictionary);
	}

	private static class DictionaryAsMap<K, V> extends AbstractMap<K,V> {
		private final Dictionary<K,V> dict;

		@SuppressWarnings("unchecked")
		DictionaryAsMap(Dictionary< ? extends K, ? extends V> dict) {
			this.dict = (Dictionary<K,V>) requireNonNull(dict);
		}

		Iterator<K> keys() {
			List<K> keys = new ArrayList<>(dict.size());
			for (Enumeration<K> e = dict.keys(); e.hasMoreElements();) {
				keys.add(e.nextElement());
			}
			return keys.iterator();
		}

		@Override
		public int size() {
			return dict.size();
		}

		@Override
		public boolean isEmpty() {
			return dict.isEmpty();
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
			return dict.put(
					requireNonNull(key,
							"a Dictionary cannot contain a null key"),
					requireNonNull(value,
							"a Dictionary cannot contain a null value"));
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
			for (Iterator<K> iter = keys(); iter.hasNext();) {
				dict.remove(iter.next());
			}
		}

		@Override
		public Set<K> keySet() {
			return new KeySet();
		}

		@Override
		public Set<Map.Entry<K,V>> entrySet() {
			return new EntrySet();
		}

		@Override
		public String toString() {
			return dict.toString();
		}

		final class KeySet extends AbstractSet<K> {
			@Override
			public Iterator<K> iterator() {
				return new KeyIterator();
			}

			@Override
			public int size() {
				return DictionaryAsMap.this.size();
			}

			@Override
			public boolean isEmpty() {
				return DictionaryAsMap.this.isEmpty();
			}

			@Override
			public boolean contains(Object key) {
				return DictionaryAsMap.this.containsKey(key);
			}

			@Override
			public boolean remove(Object key) {
				return DictionaryAsMap.this.remove(key) != null;
			}

			@Override
			public void clear() {
				DictionaryAsMap.this.clear();
			}
		}

		final class KeyIterator implements Iterator<K> {
			private final Iterator<K>	keys	= DictionaryAsMap.this.keys();
			private K					key		= null;

			@Override
			public boolean hasNext() {
				return keys.hasNext();
			}

			@Override
			public K next() {
				return key = keys.next();
			}

			@Override
			public void remove() {
				if (key == null) {
					throw new IllegalStateException();
				}
				DictionaryAsMap.this.remove(key);
				key = null;
			}
		}

		final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
			@Override
			public Iterator<Map.Entry<K,V>> iterator() {
				return new EntryIterator();
			}

			@Override
			public int size() {
				return DictionaryAsMap.this.size();
			}

			@Override
			public boolean isEmpty() {
				return DictionaryAsMap.this.isEmpty();
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
				return Objects.equals(DictionaryAsMap.this.get(key), value);
			}

			@Override
			public boolean remove(Object o) {
				if (o instanceof Map.Entry) {
					Map.Entry< ? , ? > e = (Map.Entry< ? , ? >) o;
					if (containsEntry(e)) {
						DictionaryAsMap.this.remove(e.getKey());
						return true;
					}
				}
				return false;
			}

			@Override
			public void clear() {
				DictionaryAsMap.this.clear();
			}
		}

		final class EntryIterator implements Iterator<Map.Entry<K,V>> {
			private final Iterator<K>	keys	= DictionaryAsMap.this.keys();
			private K					key		= null;

			@Override
			public boolean hasNext() {
				return keys.hasNext();
			}

			@Override
			public Map.Entry<K,V> next() {
				return new Entry(key = keys.next());
			}

			@Override
			public void remove() {
				if (key == null) {
					throw new IllegalStateException();
				}
				DictionaryAsMap.this.remove(key);
				key = null;
			}
		}

		final class Entry extends SimpleEntry<K,V> {
			private static final long serialVersionUID = 1L;

			Entry(K key) {
				super(key, DictionaryAsMap.this.get(key));
			}

			@Override
			public V setValue(V value) {
				DictionaryAsMap.this.put(getKey(), value);
				return super.setValue(value);
			}
		}
	}

	/**
	 * Return a Dictionary wrapper around a Map.
	 *
	 * @param <K> The type of the key.
	 * @param <V> The type of the value.
	 * @param map The map to wrap.
	 * @return A Dictionary object which wraps the specified map. If the
	 *         specified map can be cast to a Dictionary, then the specified map
	 *         is returned.
	 */
	public static <K, V> Dictionary<K,V> asDictionary(
			Map< ? extends K, ? extends V> map) {
		if (map instanceof Dictionary) {
			@SuppressWarnings("unchecked")
			Dictionary<K,V> coerced = (Dictionary<K,V>) map;
			return coerced;
		}
		return new MapAsDictionary<>(map);
	}

	private static class MapAsDictionary<K, V> extends Dictionary<K,V> {
		private final Map<K,V> map;

		@SuppressWarnings("unchecked")
		MapAsDictionary(Map< ? extends K, ? extends V> map) {
			this.map = (Map<K,V>) requireNonNull(map);
			if (map.containsKey(null)) {
				throw new NullPointerException(
						"a Dictionary cannot contain a null key");
			}
			if (map.containsValue(null)) {
				throw new NullPointerException(
						"a Dictionary cannot contain a null value");
			}
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		@Override
		public Enumeration<K> keys() {
			return Collections.enumeration(map.keySet());
		}

		@Override
		public Enumeration<V> elements() {
			return Collections.enumeration(map.values());
		}

		@Override
		public V get(Object key) {
			if (key == null) {
				return null;
			}
			return map.get(key);
		}

		@Override
		public V put(K key, V value) {
			return map.put(
					requireNonNull(key,
							"a Dictionary cannot contain a null key"),
					requireNonNull(value,
							"a Dictionary cannot contain a null value"));
		}

		@Override
		public V remove(Object key) {
			if (key == null) {
				return null;
			}
			return map.remove(key);
		}

		@Override
		public String toString() {
			return map.toString();
		}
	}
}

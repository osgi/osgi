/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package java.util;
public class Collections {
	public final static java.util.List EMPTY_LIST; static { EMPTY_LIST = null; }
	public final static java.util.Map EMPTY_MAP; static { EMPTY_MAP = null; }
	public final static java.util.Set EMPTY_SET; static { EMPTY_SET = null; }
	public static <T> boolean addAll(java.util.Collection<? super T> var0, T[] var1) { return false; }
	public static <T> int binarySearch(java.util.List<? extends java.lang.Comparable<? super T>> var0, T var1) { return 0; }
	public static <T> int binarySearch(java.util.List<? extends T> var0, T var1, java.util.Comparator<? super T> var2) { return 0; }
	public static <E> java.util.Collection<E> checkedCollection(java.util.Collection<E> var0, java.lang.Class<E> var1) { return null; }
	public static <E> java.util.List<E> checkedList(java.util.List<E> var0, java.lang.Class<E> var1) { return null; }
	public static <K,V> java.util.Map<K,V> checkedMap(java.util.Map<K,V> var0, java.lang.Class<K> var1, java.lang.Class<V> var2) { return null; }
	public static <E> java.util.Set<E> checkedSet(java.util.Set<E> var0, java.lang.Class<E> var1) { return null; }
	public static <K,V> java.util.SortedMap<K,V> checkedSortedMap(java.util.SortedMap<K,V> var0, java.lang.Class<K> var1, java.lang.Class<V> var2) { return null; }
	public static <E> java.util.SortedSet<E> checkedSortedSet(java.util.SortedSet<E> var0, java.lang.Class<E> var1) { return null; }
	public static <T> void copy(java.util.List<? super T> var0, java.util.List<? extends T> var1) { }
	public static boolean disjoint(java.util.Collection<?> var0, java.util.Collection<?> var1) { return false; }
	public final static <T> java.util.List<T> emptyList() { return null; }
	public final static <K,V> java.util.Map<K,V> emptyMap() { return null; }
	public final static <T> java.util.Set<T> emptySet() { return null; }
	public static <T> java.util.Enumeration<T> enumeration(java.util.Collection<T> var0) { return null; }
	public static <T> void fill(java.util.List<? super T> var0, T var1) { }
	public static int frequency(java.util.Collection<?> var0, java.lang.Object var1) { return 0; }
	public static int indexOfSubList(java.util.List<?> var0, java.util.List<?> var1) { return 0; }
	public static int lastIndexOfSubList(java.util.List<?> var0, java.util.List<?> var1) { return 0; }
	public static <T> java.util.ArrayList<T> list(java.util.Enumeration<T> var0) { return null; }
	public static <T extends java.lang.Object & java.lang.Comparable<? super T>> T max(java.util.Collection<? extends T> var0) { return null; }
	public static <T> T max(java.util.Collection<? extends T> var0, java.util.Comparator<? super T> var1) { return null; }
	public static <T extends java.lang.Object & java.lang.Comparable<? super T>> T min(java.util.Collection<? extends T> var0) { return null; }
	public static <T> T min(java.util.Collection<? extends T> var0, java.util.Comparator<? super T> var1) { return null; }
	public static <T> java.util.List<T> nCopies(int var0, T var1) { return null; }
	public static <T> boolean replaceAll(java.util.List<T> var0, T var1, T var2) { return false; }
	public static void reverse(java.util.List<?> var0) { }
	public static <T> java.util.Comparator<T> reverseOrder() { return null; }
	public static <T> java.util.Comparator<T> reverseOrder(java.util.Comparator<T> var0) { return null; }
	public static void rotate(java.util.List<?> var0, int var1) { }
	public static void shuffle(java.util.List<?> var0) { }
	public static void shuffle(java.util.List<?> var0, java.util.Random var1) { }
	public static <T> java.util.Set<T> singleton(T var0) { return null; }
	public static <T> java.util.List<T> singletonList(T var0) { return null; }
	public static <K,V> java.util.Map<K,V> singletonMap(K var0, V var1) { return null; }
	public static <T extends java.lang.Comparable<? super T>> void sort(java.util.List<T> var0) { }
	public static <T> void sort(java.util.List<T> var0, java.util.Comparator<? super T> var1) { }
	public static void swap(java.util.List<?> var0, int var1, int var2) { }
	public static <T> java.util.Collection<T> synchronizedCollection(java.util.Collection<T> var0) { return null; }
	public static <T> java.util.List<T> synchronizedList(java.util.List<T> var0) { return null; }
	public static <K,V> java.util.Map<K,V> synchronizedMap(java.util.Map<K,V> var0) { return null; }
	public static <T> java.util.Set<T> synchronizedSet(java.util.Set<T> var0) { return null; }
	public static <K,V> java.util.SortedMap<K,V> synchronizedSortedMap(java.util.SortedMap<K,V> var0) { return null; }
	public static <T> java.util.SortedSet<T> synchronizedSortedSet(java.util.SortedSet<T> var0) { return null; }
	public static <T> java.util.Collection<T> unmodifiableCollection(java.util.Collection<? extends T> var0) { return null; }
	public static <T> java.util.List<T> unmodifiableList(java.util.List<? extends T> var0) { return null; }
	public static <K,V> java.util.Map<K,V> unmodifiableMap(java.util.Map<? extends K,? extends V> var0) { return null; }
	public static <T> java.util.Set<T> unmodifiableSet(java.util.Set<? extends T> var0) { return null; }
	public static <K,V> java.util.SortedMap<K,V> unmodifiableSortedMap(java.util.SortedMap<K,? extends V> var0) { return null; }
	public static <T> java.util.SortedSet<T> unmodifiableSortedSet(java.util.SortedSet<T> var0) { return null; }
	private Collections() { } /* generated constructor to prevent compiler adding default public constructor */
}


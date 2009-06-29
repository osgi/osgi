/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.blueprint.components.injection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.SortedSet;
import java.util.SortedMap;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class PropertyInjection extends BaseTestComponent {

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public PropertyInjection() {
        super();
    }

    public void setString(String value) {
        setPropertyValue("string", value, String.class);
    }

    public void setPrimBoolean(boolean value) {
        setPropertyValue("primBoolean", value);
    }

    public void setPrimByte(byte value) {
        setPropertyValue("primByte", value);
    }

    public void setPrimCharacter(char value) {
        setPropertyValue("primCharacter", value);
    }

    public void setPrimDouble(double value) {
        setPropertyValue("primDouble", value);
    }

    public void setPrimFloat(float value) {
        setPropertyValue("primFloat", value);
    }

    public void setPrimInteger(int value) {
        setPropertyValue("primInteger", value);
    }

    public void setPrimLong(long value) {
        setPropertyValue("primLong", value);
    }

    public void setPrimShort(short value) {
        setPropertyValue("primShort", value);
    }

    public void setBoolean(Boolean value) {
        setPropertyValue("boolean", value, Boolean.class);
    }

    public void setByte(Byte value) {
        setPropertyValue("byte", value, Byte.class);
    }

    public void setCharacter(Character value) {
        setPropertyValue("character", value, Character.class);
    }

    public void setDouble(Double value) {
        setPropertyValue("double", value, Double.class);
    }

    public void setFloat(Float value) {
        setPropertyValue("float", value, Float.class);
    }

    public void setInteger(Integer value) {
        setPropertyValue("integer", value, Integer.class);
    }

    public void setLong(Long value) {
        setPropertyValue("long", value, Long.class);
    }

    public void setShort(Short value) {
        setPropertyValue("short", value, Short.class);
    }

    public void setClassProp(Class value) {
        setPropertyValue("classProp", value, Class.class);
    }

    public void setLocale(Locale value) {
        setPropertyValue("locale", value, Locale.class);
    }

    public void setUrl(URL value) {
        setPropertyValue("url", value, URL.class);
    }

    public void setProperties(Properties value) {
        setPropertyValue("properties", value, Properties.class);
        if (value != null) {
            value.setProperty("$$$$$$ABC$$$$$$", "abc");
            value.remove("$$$$$$ABC$$$$$$");
        }
    }

    public void setDate(Date value) {
        setPropertyValue("date", value, Date.class);
    }

    public void setMap(Map value) {
        setPropertyValue("map", value, Map.class);
        // ensure this is a mutable version, so add a unique element and remove it
        if (value != null) {
            value.put("$$$$$$ABC$$$$$$", "abc");
            value.remove("$$$$$$ABC$$$$$$");
        }
    }

    public void setSet(Set value) {
        setPropertyValue("set", value, Set.class);
        // ensure this is a mutable version, so add a unique element and remove it
        if (value != null) {
            value.add("$$$$$$ABC$$$$$$");
            value.remove("$$$$$$ABC$$$$$$");
        }
    }

    public void setList(List value) {
        setPropertyValue("list", value, List.class);
        // ensure this is a mutable version, so add a unique element and remove it
        if (value != null) {
            value.add("$$$$$$ABC$$$$$$");
            value.remove("$$$$$$ABC$$$$$$");
        }
    }

    public void setCollection(Collection value) {
        setPropertyValue("collection", value, Collection.class);
    }

    public void setCollectionSubTypeImpl(CollectionSubTypeImpl value) {
        setPropertyValue("collectionSubTypeImpl", value, CollectionSubTypeImpl.class);
    }

    public void setMapSubTypeImpl(MapSubTypeImpl value) {
        setPropertyValue("mapSubTypeImpl", value, MapSubTypeImpl.class);
    }

    public void setDictionarySubTypeImpl(DictionarySubTypeImpl value) {
        setPropertyValue("dictionarySubTypeImpl", value, DictionarySubTypeImpl.class);
    }

    public void setSortedSet(SortedSet value) {
        setPropertyValue("sortedSet", value, SortedSet.class);
    }

    public void setStack(Stack value) {
        setPropertyValue("stack", value, Stack.class);
    }

    public void setArrayList(ArrayList value) {
        setPropertyValue("arrayList", value, ArrayList.class);
    }

    public void setLinkedList(LinkedList value) {
        setPropertyValue("linkedList", value, LinkedList.class);
    }

    public void setVector(Vector value) {
        setPropertyValue("vector", value, Vector.class);
    }

    public void setHashSet(HashSet value) {
        setPropertyValue("hashSet", value, HashSet.class);
    }

    public void setLinkedHashSet(LinkedHashSet value) {
        setPropertyValue("linkedHashSet", value, LinkedHashSet.class);
    }

    public void setTreeSet(TreeSet value) {
        setPropertyValue("treeSet", value, TreeSet.class);
    }

    public void setHashMap(HashMap value) {
        setPropertyValue("hashMap", value, HashMap.class);
    }

    public void setHashtable(Hashtable value) {
        setPropertyValue("hashtable", value, Hashtable.class);
    }

    public void setTreeMap(TreeMap value) {
        setPropertyValue("treeMap", value, TreeMap.class);
    }

    public void setDictionary(Dictionary value) {
        setPropertyValue("dictionary", value, Dictionary.class);
    }

    public void setSortedMap(SortedMap value) {
        setPropertyValue("sortedMap", value, SortedMap.class);
    }

    // Start of insertion of array property
    public void setObjectArray(Object[] arr) {
        setPropertyValue("emptyArray", arr, Object[].class);
    }

    public void setNullArray(Object[] arr) {
        setPropertyValue("nullArray", arr, Object[].class);
    }

    public void setStringArray(String[] arr) {
        setPropertyValue("stringArray", arr, String[].class);
    }

    public void setPrimIntArray(int[] arr) {
        setPropertyValue("primIntArray", arr, int[].class);
    }

    public void setWrappedIntArray(Integer[] arr) {
        setPropertyValue("wrappedIntArray", arr, Integer[].class);
    }

    public void setPrimBoolArray(boolean[] arr) {
        setPropertyValue("primBoolArray", arr, boolean[].class);
    }

    public void setWrappedBooleanArray(Boolean[] arr) {
        setPropertyValue("wrappedBooleanArray", arr, Boolean[].class);
    }

    public void setPrimByteArray(byte[] arr) {
        setPropertyValue("primByteArray", arr, byte[].class);
    }

    public void setWrappedByteArray(Byte[] arr) {
        setPropertyValue("wrappedByteArray", arr, Byte[].class);
    }

    public void setPrimCharArray(char[] arr) {
        setPropertyValue("primCharArray", arr, char[].class);
    }

    public void setWrappedCharArray(Character[] arr) {
        setPropertyValue("wrappedCharArray", arr, Character[].class);
    }

    public void setPrimShortArray(short[] arr) {
        setPropertyValue("primShortArray", arr, short[].class);
    }

    public void setWrappedShortArray(Short[] arr) {
        setPropertyValue("wrappedShortArray", arr, Short[].class);
    }

    public void setPrimLongArray(long[] arr) {
        setPropertyValue("primLongArray", arr, long[].class);
    }

    public void setWrappedLongArray(Long[] arr) {
        setPropertyValue("wrappedLongArray", arr, Long[].class);
    }

    public void setPrimDoubleArray(double[] arr) {
        setPropertyValue("primDoubleArray", arr, double[].class);
    }

    public void setWrappedDoubleArray(Double[] arr) {
        setPropertyValue("wrappedDoubleArray", arr, Double[].class);
    }

    public void setPrimFloatArray(float[] arr) {
        setPropertyValue("primFloatArray", arr, float[].class);
    }

    public void setWrappedFloatArray(Float[] arr) {
        setPropertyValue("wrappedFloatArray", arr, Float[].class);
    }

    public void setDateArray(Date[] arr) {
        setPropertyValue("dateArray", arr, Date[].class);
    }

    public void setUrlArray(URL[] arr) {
        setPropertyValue("urlArray", arr, URL[].class);
    }

    public void setClassArray(Class[] arr) {
        setPropertyValue("classArray", arr, Class[].class);
    }

    public void setLocaleArray(Locale[] arr) {
        setPropertyValue("localeArray", arr, Locale[].class);
    }

    public void setPropsArray(Properties[] arr) {
        setPropertyValue("propsArray", arr, Properties[].class);
    }

    public void setMapArray(Map[] arr) {
        setPropertyValue("mapArray", arr, Map[].class);
    }

    public void setSetArray(Set[] arr) {
        setPropertyValue("setArray", arr, Set[].class);
    }

    public void setListArray(List[] arr) {
        setPropertyValue("listArray", arr, List[].class);
    }

    public void setNestedArray(Object[] arr) {
        setPropertyValue("nestedArray", arr, Object[].class);
    }

    public void setRegionCode(RegionCode v) {
        setPropertyValue("regionCode", v, RegionCode.class);
    }

    public void setAsianRegionCode(RegionCode v) {
        setPropertyValue("asianRegionCode", v, AsianRegionCode.class);
    }

    public void setRegionCodeArray(RegionCode[] v) {
        setPropertyValue("regionCodeArray", v, RegionCode[].class);
    }


    // End of insertion of array property

    // the following ones are to test property error situations

    // more than one setter
    public void setAmbiguous(Object v) {
        setPropertyValue("ambiguous", v, Object.class);
    }

    public void setAmbiguous(int v) {
        setPropertyValue("ambiguous", v);
    }

    // mismatched setters/getters
    public void setBadBean(int v) {
        setPropertyValue("badBean", v);
    }

    public Object getBadBean() {
        return null;     // not really used
    }

    // protected setter
    protected void setProtected(String v) {
        setPropertyValue("protected", v, String.class);
    }

    // private setter
    private void setPrivate(String v) {
        setPropertyValue("private", v, String.class);
    }
}

/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.components.injection;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

    public void setClass(Class value) {
        setPropertyValue("class", value, Class.class);
    }

    public void setLocale(Locale value) {
        setPropertyValue("locale", value, Locale.class);
    }

    public void setUrl(URL value) {
        setPropertyValue("url", value, URL.class);
    }

    public void setProperties(Properties value) {
        setPropertyValue("properties", value, Properties.class);
    }

    public void setDate(Date value) {
        setPropertyValue("date", value, Date.class);
    }

    public void setMap(Map value) {
        setPropertyValue("map", value, Map.class);
    }

    public void setSet(Set value) {
        setPropertyValue("set", value, Set.class);
    }

    public void setList(List value) {
        setPropertyValue("list", value, List.class);
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
        setPropertyValue("wrappedShortArray", arr, Long[].class);
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

    // End of insertion of array property
}

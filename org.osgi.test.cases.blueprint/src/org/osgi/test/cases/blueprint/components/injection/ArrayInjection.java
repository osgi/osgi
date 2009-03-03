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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.List;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class ArrayInjection extends BaseTestComponent {

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ArrayInjection() {
        super();
    }

    /**
     * Simple injection with a converted array argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ArrayInjection(int[] arg1) {
        super();
        setArgumentValue("arg1", arg1, new int[0].getClass());
    }

    public void setString(String[] arg1) {
        setPropertyValue("string", arg1, new String[0].getClass());
    }

    public void setPrimBoolean(boolean[] arg1) {
        setPropertyValue("primBoolean", arg1, new boolean[0].getClass());
    }

    public void setPrimByte(byte[] arg1) {
        setPropertyValue("primByte", arg1, new byte[0].getClass());
    }

    public void setPrimChar(char[] arg1) {
        setPropertyValue("primChar", arg1, new char[0].getClass());
    }

    public void setPrimDouble(double[] arg1) {
        setPropertyValue("primDouble", arg1, new double[0].getClass());
    }

    public void setPrimFloat(float[] arg1) {
        setPropertyValue("primFloat", arg1, new float[0].getClass());
    }

    public void setPrimInt(int[] arg1) {
        setPropertyValue("primInt", arg1, new int[0].getClass());
    }

    public void setPrimLong(long[] arg1) {
        setPropertyValue("primLong", arg1, new long[0].getClass());
    }

    public void setPrimShort(short[] arg1) {
        setPropertyValue("primShort", arg1, new short[0].getClass());
    }

    public void setBoolean(Boolean[] arg1) {
        setPropertyValue("boolean", arg1, new Boolean[0].getClass());
    }

    public void setByte(Byte[] arg1) {
        setPropertyValue("byte", arg1, new Byte[0].getClass());
    }

    public void setChar(Character[] arg1) {
        setPropertyValue("char", arg1, new Character[0].getClass());
    }

    public void setDouble(Double[] arg1) {
        setPropertyValue("double", arg1, new Double[0].getClass());
    }

    public void setFloat(Float[] arg1) {
        setPropertyValue("float", arg1, new Float[0].getClass());
    }

    public void setInt(Integer[] arg1) {
        setPropertyValue("int", arg1, new Integer[0].getClass());
    }

    public void setLong(Long[] arg1) {
        setPropertyValue("long", arg1, new Long[0].getClass());
    }

    public void setShort(Short[] arg1) {
        setPropertyValue("short", arg1, new Short[0].getClass());
    }

    public void setClass(Class[] arg1) {
        setPropertyValue("class", arg1, new Class[0].getClass());
    }

    public void setLocale(Locale[] arg1) {
        setPropertyValue("locale", arg1, new Locale[0].getClass());
    }

    public void setUrl(URL[] arg1) {
        setPropertyValue("url", arg1, new URL[0].getClass());
    }

    public void setProperties(Properties[] arg1) {
        setPropertyValue("properties", arg1, new Properties[0].getClass());
    }

    public void setMap(Map[] arg1) {
        setPropertyValue("map", arg1, new Map[0].getClass());
    }

    public void setSet(Set[] arg1) {
        setPropertyValue("set", arg1, new Set[0].getClass());
    }

    public void setSet(List[] arg1) {
        setPropertyValue("list", arg1, new List[0].getClass());
    }
}


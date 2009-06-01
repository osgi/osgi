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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.List;

import org.osgi.test.cases.blueprint.services.ObjectArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.IntArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.ByteArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.BooleanArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.CharArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.ShortArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.LongArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.DoubleArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.FloatArrayValueDescriptor;

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
        setArgumentValue("arg1", new IntArrayValueDescriptor("arg1", arg1));
    }

    public void setString(String[] arg1) {
        setPropertyValue("string", new ObjectArrayValueDescriptor("string", arg1));
    }

    public void setPrimBoolean(boolean[] arg1) {
        setPropertyValue("primBoolean", new BooleanArrayValueDescriptor("primBoolean", arg1));
    }

    public void setPrimByte(byte[] arg1) {
        setPropertyValue("primByte", new ByteArrayValueDescriptor("primByte", arg1));
    }

    public void setPrimChar(char[] arg1) {
        setPropertyValue("primChar", new CharArrayValueDescriptor("primChar", arg1));
    }

    public void setPrimDouble(double[] arg1) {
        setPropertyValue("primDouble", new DoubleArrayValueDescriptor("primDouble", arg1));
    }

    public void setPrimFloat(float[] arg1) {
        setPropertyValue("primFloat", new FloatArrayValueDescriptor("primFloat", arg1));
    }

    public void setPrimInt(int[] arg1) {
        setPropertyValue("primInt", new IntArrayValueDescriptor("primInt", arg1));
    }

    public void setPrimLong(long[] arg1) {
        setPropertyValue("primLong", new LongArrayValueDescriptor("primLong", arg1));
    }

    public void setPrimShort(short[] arg1) {
        setPropertyValue("primShort", new ShortArrayValueDescriptor("primShort", arg1));
    }

    public void setBoolean(Boolean[] arg1) {
        setPropertyValue("boolean", new ObjectArrayValueDescriptor("boolean", arg1));
    }

    public void setByte(Byte[] arg1) {
        setPropertyValue("byte", new ObjectArrayValueDescriptor("byte", arg1));
    }

    public void setChar(Character[] arg1) {
        setPropertyValue("char", new ObjectArrayValueDescriptor("char", arg1));
    }

    public void setDouble(Double[] arg1) {
        setPropertyValue("double", new ObjectArrayValueDescriptor("double", arg1));
    }

    public void setFloat(Float[] arg1) {
        setPropertyValue("float", new ObjectArrayValueDescriptor("float", arg1));
    }

    public void setInt(Integer[] arg1) {
        setPropertyValue("int", new ObjectArrayValueDescriptor("int", arg1));
    }

    public void setLong(Long[] arg1) {
        setPropertyValue("long", new ObjectArrayValueDescriptor("long", arg1));
    }

    public void setShort(Short[] arg1) {
        setPropertyValue("short", new ObjectArrayValueDescriptor("short", arg1));
    }

    public void setClassArray(Class[] arg1) {
        setPropertyValue("class", new ObjectArrayValueDescriptor("class", arg1));
    }

    public void setLocale(Locale[] arg1) {
        setPropertyValue("locale", new ObjectArrayValueDescriptor("locale", arg1));
    }

    public void setUrl(URL[] arg1) {
        setPropertyValue("url", new ObjectArrayValueDescriptor("url", arg1));
    }

    public void setProperties(Properties[] arg1) {
        setPropertyValue("properties", new ObjectArrayValueDescriptor("properties", arg1));
    }

    public void setMap(Map[] arg1) {
        setPropertyValue("map", new ObjectArrayValueDescriptor("map", arg1));
    }

    public void setSet(Set[] arg1) {
        setPropertyValue("set", new ObjectArrayValueDescriptor("set", arg1));
    }

    public void setSet(List[] arg1) {
        setPropertyValue("list", new ObjectArrayValueDescriptor("list", arg1));
    }
}


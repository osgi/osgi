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

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class ConstructorInjection extends BaseTestComponent {

    /**
     * Simple no-arg injection
     */
    public ConstructorInjection() {
        super();
    }

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public ConstructorInjection(String arg1) {
        super("stringArg");
        setArgumentValue("arg1", arg1);
    }

    /**
     * Simple injection with two string arguments.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public ConstructorInjection(String arg1, String arg2) {
        super("twoStringArgs");
        setArgumentValue("arg1", arg1);
        setArgumentValue("arg2", arg2, String.class);
    }

    /**
     * Simple injection with an third argument that's a very non-specific object. Use to test forced coercion.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public ConstructorInjection(String arg1, String arg2, Object arg3) {
        super("threeMixedArgs");
        setArgumentValue("arg1", arg1);
        setArgumentValue("arg2", arg2, String.class);
        // NB. This test cannot use NULL as a third argument because the
        // recorded class is taken from the argument value.
        setArgumentValue("arg3", arg3);
    }

    public ConstructorInjection(boolean arg1) {
        super("booleanArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(byte arg1) {
        super("byteArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(char arg1) {
        super("charArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(double arg1) {
        super("doubleArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(float arg1) {
        super("floatArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(int arg1) {
        super("intArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(long arg1) {
        super("longArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(short arg1) {
        super("shortArg");
        setArgumentValue("arg1", arg1);
    }

    public ConstructorInjection(Boolean arg1) {
        super("BooleanArg");
        // need to set this explicitly for the null tests
        setArgumentValue("arg1", arg1, Boolean.class);
    }

    public ConstructorInjection(Byte arg1) {
        super("ByteArg");
        setArgumentValue("arg1", arg1, Byte.class);
    }

    public ConstructorInjection(Character arg1) {
        super("CharacterArg");
        setArgumentValue("arg1", arg1, Character.class);
    }

    public ConstructorInjection(Double arg1) {
        super("DoubleArg");
        setArgumentValue("arg1", arg1, Double.class);
    }

    public ConstructorInjection(Float arg1) {
        super("FloatArg");
        setArgumentValue("arg1", arg1, Float.class);
    }

    public ConstructorInjection(Integer arg1) {
        super("IntegerArg");
        setArgumentValue("arg1", arg1, Integer.class);
    }

    public ConstructorInjection(Long arg1) {
        super("LongArg");
        setArgumentValue("arg1", arg1, Long.class);
    }

    public ConstructorInjection(Short arg1) {
        super("ShortArg");
        setArgumentValue("arg1", arg1, Short.class);
    }

    public ConstructorInjection(Class arg1) {
        super("ClassArg");
        setArgumentValue("arg1", arg1, Class.class);
    }

    public ConstructorInjection(File arg1) {
        super("FileArg");
        setArgumentValue("arg1", arg1, File.class);
    }

    public ConstructorInjection(Locale arg1) {
        super("LocaleArg");
        setArgumentValue("arg1", arg1, Locale.class);
    }

    public ConstructorInjection(URL arg1) {
        super("URLArg");
        setArgumentValue("arg1", arg1, URL.class);
    }

    public ConstructorInjection(Properties arg1) {
        super("PropertiesArg");
        setArgumentValue("arg1", arg1, Properties.class);
    }

    public ConstructorInjection(Date arg1) {
        super("DateArg");
        setArgumentValue("arg1", arg1, Date.class);
    }

    public ConstructorInjection(Map arg1) {
        super("MapArg");
        setArgumentValue("arg1", arg1, Map.class);
    }

    public ConstructorInjection(Set arg1) {
        super("SetArg");
        setArgumentValue("arg1", arg1, Set.class);
    }

    public ConstructorInjection(List arg1) {
        super("ListArg");
        setArgumentValue("arg1", arg1, List.class);
    }

    // Array constructor argument
    public ConstructorInjection(Object[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Object[].class);
    }

    public ConstructorInjection(int[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, int[].class);
    }

    public ConstructorInjection(Integer[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Integer[].class);
    }

    public ConstructorInjection(boolean[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, boolean[].class);
    }

    public ConstructorInjection(Boolean[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Boolean[].class);
    }

    public ConstructorInjection(String[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, String[].class);
    }

    public ConstructorInjection(byte[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, byte[].class);
    }

    public ConstructorInjection(Byte[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Byte[].class);
    }

    public ConstructorInjection(char[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, char[].class);
    }

    public ConstructorInjection(Character[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Character[].class);
    }

    public ConstructorInjection(short[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, short[].class);
    }

    public ConstructorInjection(Short[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Short[].class);
    }

    public ConstructorInjection(long[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, long[].class);
    }

    public ConstructorInjection(Long[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Long[].class);
    }

    public ConstructorInjection(double[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, double[].class);
    }

    public ConstructorInjection(Double[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Double[].class);
    }

    public ConstructorInjection(float[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, float[].class);
    }

    public ConstructorInjection(Float[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Float[].class);
    }

    public ConstructorInjection(Date[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Date[].class);
    }

    public ConstructorInjection(URL[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, URL[].class);
    }

    public ConstructorInjection(Class[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Class[].class);
    }

    public ConstructorInjection(Locale[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Locale[].class);
    }

    public ConstructorInjection(Properties[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Properties[].class);
    }

    public ConstructorInjection(Map[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Map[].class);
    }

    public ConstructorInjection(List[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, List[].class);
    }

    public ConstructorInjection(Set[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Set[].class);
    }
}

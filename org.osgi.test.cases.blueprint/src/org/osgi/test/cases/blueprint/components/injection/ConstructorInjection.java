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

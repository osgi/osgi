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

public class ConstructorInjectionInstanceFactory extends BaseTestComponent {

    /**
     * Simple injection with no arguments.
     */
    public Object makeInstance() {
        return new ConstructorInjection(componentId);
    }

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public Object makeInstance(String componentId) {
        return new ConstructorInjection(componentId);
    }

    /**
     * Simple injection with two string arguments.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public Object makeInstance(String componentId, String arg2) {
        return new ConstructorInjection(componentId, arg2);
    }

    /**
     * Simple injection with an third argument that's a very non-specific object. Use to test forced coercion.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public Object makeInstance(String componentId, String arg2, Object arg3) {
        return new ConstructorInjection(componentId, arg2, arg3);
    }

    public Object makeInstance(boolean arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(byte arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(char arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(double arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(float arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(int arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(long arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(short arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Boolean arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Byte arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Character arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Double arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Float arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Integer arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Long arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Short arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Class arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(File arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Locale arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(URL arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Properties arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Date arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Map arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(Set arg2) {
        return new ConstructorInjection(arg2);
    }

    public Object makeInstance(List arg2) {
        return new ConstructorInjection(arg2);
    }

    // for implicit injection test
    public Object makePrimitiveBoolean(boolean arg1) {
        return new PrimitiveBooleanInjection(arg1);
    }

    public Object makePrimitiveByte(byte arg1) {
        return new PrimitiveByteInjection(arg1);
    }

    public Object makePrimitiveCharacter(char arg1) {
        return new PrimitiveCharacterInjection(arg1);
    }

    public Object makePrimitiveInteger(int arg1) {
        return new PrimitiveIntegerInjection(arg1);
    }

    public Object makePrimitiveLong(long arg1) {
        return new PrimitiveLongInjection(arg1);
    }

    public Object makePrimitiveShort(short arg1) {
        return new PrimitiveShortInjection(arg1);
    }

    public Object makePrimitiveDouble(double arg1) {
        return new PrimitiveDoubleInjection(arg1);
    }

    public Object makePrimitiveFloat(float arg1) {
        return new PrimitiveFloatInjection(arg1);
    }

    public Object makeWrapperedBoolean(Boolean arg1) {
        return new WrapperedBooleanInjection(arg1);
    }

    public Object makeWrapperedByte(Byte arg1) {
        return new WrapperedByteInjection(arg1);
    }

    public Object makeWrapperedCharacter(Character arg1) {
        return new WrapperedCharacterInjection(arg1);
    }

    public Object makeWrapperedInteger(Integer arg1) {
        return new WrapperedIntegerInjection(arg1);
    }

    public Object makeWrapperedLong(Long arg1) {
        return new WrapperedLongInjection(arg1);
    }

    public Object makeWrapperedShort(Short arg1) {
        return new WrapperedShortInjection(arg1);
    }

    public Object makeWrapperedDouble(Double arg1) {
        return new WrapperedDoubleInjection(arg1);
    }

    public Object makeWrapperedFloat(Float arg1) {
        return new WrapperedFloatInjection(arg1);
    }

    // Factory Injection of array
    public Object makeInstance(Object[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(String[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(int[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Integer[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(boolean[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Boolean[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(byte[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Byte[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(char[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Character[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(short[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Short[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(long[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Long[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(double[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Double[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(float[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Float[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Date[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(URL[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Class[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Locale[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(List[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Set[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Map[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(Properties[] arg1) {
        return new ConstructorInjection(arg1);
    }
}

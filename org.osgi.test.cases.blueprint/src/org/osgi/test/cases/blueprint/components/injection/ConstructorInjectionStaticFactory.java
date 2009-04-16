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

public class ConstructorInjectionStaticFactory {

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    static public Object makeInstance(String componentId) {
        return new ConstructorInjection(componentId);
    }

    /**
     * Simple injection with a no arguments
     */
    static public Object makeInstance() {
        return new ConstructorInjection();
    }

    /**
     * Simple injection with two string arguments.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    static public Object makeInstance(String componentId, String arg2) {
        return new ConstructorInjection(componentId, arg2);
    }

    /**
     * Simple injection with an third argument that's a very non-specific object. Use to test forced coercion.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    static public Object makeInstance(String componentId, String arg2, Object arg3) {
        return new ConstructorInjection(componentId, arg2, arg3);
    }

    static public Object makeInstance(boolean arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(byte arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(char arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(double arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(float arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(int arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(long arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(short arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Boolean arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Byte arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Character arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Double arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Float arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Integer arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Long arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Short arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Class arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(File arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Locale arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(URL arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Properties arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Date arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Map arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(Set arg2) {
        return new ConstructorInjection(arg2);
    }

    static public Object makeInstance(List arg2) {
        return new ConstructorInjection(arg2);
    }

    // for implicit injection test
    static public Object makePrimitiveBoolean(boolean arg1) {
        return new PrimitiveBooleanInjection(arg1);
    }

    static public Object makePrimitiveByte(byte arg1) {
        return new PrimitiveByteInjection(arg1);
    }

    static public Object makePrimitiveCharacter(char arg1) {
        return new PrimitiveCharacterInjection(arg1);
    }

    static public Object makePrimitiveInteger(int arg1) {
        return new PrimitiveIntegerInjection(arg1);
    }

    static public Object makePrimitiveLong(long arg1) {
        return new PrimitiveLongInjection(arg1);
    }

    static public Object makePrimitiveShort(short arg1) {
        return new PrimitiveShortInjection(arg1);
    }

    static public Object makePrimitiveDouble(double arg1) {
        return new PrimitiveDoubleInjection(arg1);
    }

    static public Object makePrimitiveFloat(float arg1) {
        return new PrimitiveFloatInjection(arg1);
    }

    static public Object makeWrapperedBoolean(Boolean arg1) {
        return new WrapperedBooleanInjection(arg1);
    }

    static public Object makeWrapperedByte(Byte arg1) {
        return new WrapperedByteInjection(arg1);
    }

    static public Object makeWrapperedCharacter(Character arg1) {
        return new WrapperedCharacterInjection(arg1);
    }

    static public Object makeWrapperedInteger(Integer arg1) {
        return new WrapperedIntegerInjection(arg1);
    }

    static public Object makeWrapperedLong(Long arg1) {
        return new WrapperedLongInjection(arg1);
    }

    static public Object makeWrapperedShort(Short arg1) {
        return new WrapperedShortInjection(arg1);
    }

    static public Object makeWrapperedDouble(Double arg1) {
        return new WrapperedDoubleInjection(arg1);
    }

    static public Object makeWrapperedFloat(Float arg1) {
        return new WrapperedFloatInjection(arg1);
    }

    // Factory Injection of array
    static public Object makeInstance(Object[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(String[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(int[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Integer[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(boolean[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Boolean[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(byte[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Byte[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(char[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Character[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(short[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Short[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(long[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Long[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(double[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Double[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(float[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Float[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Date[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(URL[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Class[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Locale[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(List[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Set[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Map[] arg1) {
        return new ConstructorInjection(arg1);
    }

    static public Object makeInstance(Properties[] arg1) {
        return new ConstructorInjection(arg1);
    }
}

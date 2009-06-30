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

    public Object makePrimInstance(boolean arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makePrimInstance(byte arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makePrimInstance(char arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makePrimInstance(double arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makePrimInstance(float arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makePrimInstance(int arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makePrimInstance(long arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makePrimInstance(short arg2) {
        return new PrimitiveConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Boolean arg2) {
        return new WrapperConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Byte arg2) {
        return new WrapperConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Character arg2) {
        return new WrapperConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Double arg2) {
        return new WrapperConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Float arg2) {
        return new WrapperConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Integer arg2) {
        return new WrapperConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Long arg2) {
        return new WrapperConstructorInjection(arg2);
    }

    public Object makeWrapperInstance(Short arg2) {
        return new WrapperConstructorInjection(arg2);
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

    public Object makeInstance(String[] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makeInstance(String[][] arg1) {
        return new ConstructorInjection(arg1);
    }

    public Object makePrimInstance(int[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Integer[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
    }

    public Object makePrimInstance(boolean[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Boolean[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
    }

    public Object makePrimInstance(byte[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Byte[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
    }

    public Object makePrimInstance(char[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Character[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
    }

    public Object makePrimInstance(short[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Short[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
    }

    public Object makePrimInstance(long[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Long[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
    }

    public Object makePrimInstance(double[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Double[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
    }

    public Object makePrimInstance(float[] arg1) {
        return new PrimitiveArrayConstructorInjection(arg1);
    }

    public Object makeWrapperInstance(Float[] arg1) {
        return new WrapperArrayConstructorInjection(arg1);
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
}

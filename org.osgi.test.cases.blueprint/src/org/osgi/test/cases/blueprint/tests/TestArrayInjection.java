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

package org.osgi.test.cases.blueprint.tests;

import org.osgi.test.cases.blueprint.framework.ArgumentValueValidator;
import org.osgi.test.cases.blueprint.framework.ArgumentMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyValueValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.TestListValue;
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestSetValue;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.framework.TestValue;
import org.osgi.test.cases.blueprint.services.BooleanArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.ByteArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.CharArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.DoubleArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.FloatArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.IntArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.LongArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.ObjectArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.ShortArrayValueDescriptor;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains primitive string to type injection convertions
 *
 * @version $Id$
 */
public class TestArrayInjection extends DefaultTestBundleControl {
    private void addPropertyValidator(MetadataEventSet startEvents, String compName, ValueDescriptor value) {
        // a "" string value
        startEvents.addValidator(new PropertyValueValidator(compName, value));
    }

	public void testArrayInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/array_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        startEvents.addValidator(new ArgumentValueValidator("compEmptyListArgument", new IntArrayValueDescriptor("arg1", new int[0])));
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyListArgument", new TestArgument(
            new TestListValue(new TestValue[0]))));
        startEvents.addValidator(new ArgumentValueValidator("compEmptySetArgument", new IntArrayValueDescriptor("arg1", new int[0])));
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptySetArgument", new TestArgument(
            new TestSetValue(new TestValue[0]))));
        // simple list of ints
        startEvents.addValidator(new ArgumentValueValidator("compListArgument", new IntArrayValueDescriptor("arg1", new int[] {1234, 5678})));
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compListArgument", new TestArgument(
            new TestListValue(new TestValue[] { new TestStringValue("1234"), new TestStringValue("5678") }))));
        // NB, Sets tend to be one item because you can't depend on order of final array
        startEvents.addValidator(new ArgumentValueValidator("compSetArgument", new IntArrayValueDescriptor("arg1", new int[] {1234})));
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compSetArgument", new TestArgument(
            new TestSetValue(new TestValue[] { new TestStringValue("1234") }))));

        // boolean conversions
        addPropertyValidator(startEvents, "compListPrimBoolean", new BooleanArrayValueDescriptor("primBoolean", new boolean[] {true, false, true, false, true, false}));
        addPropertyValidator(startEvents, "compSetPrimBoolean", new BooleanArrayValueDescriptor("primBoolean", new boolean[] {true, true, true}));
        addPropertyValidator(startEvents, "compListWrapperedBoolean", new ObjectArrayValueDescriptor("boolean", new Boolean[] {Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE}));
        addPropertyValidator(startEvents, "compSetWrapperedBoolean", new ObjectArrayValueDescriptor("boolean", new Boolean[] {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE}));
        addPropertyValidator(startEvents, "compPrimBooleanEmptyList", new BooleanArrayValueDescriptor("primBoolean", new boolean[] {}));
        addPropertyValidator(startEvents, "compPrimBooleanEmptySet", new BooleanArrayValueDescriptor("primBoolean", new boolean[] {}));

        // byte conversion
        addPropertyValidator(startEvents, "compListPrimByte", new ByteArrayValueDescriptor("primByte", new byte[] {0, Byte.MAX_VALUE, Byte.MIN_VALUE}));
        addPropertyValidator(startEvents, "compSetPrimByte", new ByteArrayValueDescriptor("primByte", new byte[] {123}));
        addPropertyValidator(startEvents, "compListWrapperedByte", new ObjectArrayValueDescriptor("byte", new Byte[] {new Byte((byte)0), new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE)}));
        addPropertyValidator(startEvents, "compSetWrapperedByte", new ObjectArrayValueDescriptor("byte", new Byte[] {new Byte((byte)123)}));

        // double conversion
        addPropertyValidator(startEvents, "compListPrimDouble", new DoubleArrayValueDescriptor("primDouble", new double[] {0, Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}));
        addPropertyValidator(startEvents, "compSetPrimDouble", new DoubleArrayValueDescriptor("primDouble", new double[] {123}));
        addPropertyValidator(startEvents, "compListWrapperedDouble", new ObjectArrayValueDescriptor("double", new Double[] {new Double(0), new Double(Double.MAX_VALUE), new Double(Double.MIN_VALUE), new Double(Double.POSITIVE_INFINITY), new Double(Double.NEGATIVE_INFINITY)}));
        addPropertyValidator(startEvents, "compSetWrapperedDouble", new ObjectArrayValueDescriptor("double", new Double[] {new Double(123)}));

        // float conversion
        addPropertyValidator(startEvents, "compListPrimFloat", new FloatArrayValueDescriptor("primFloat", new float[] {0, Float.MAX_VALUE, Float.MIN_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}));
        addPropertyValidator(startEvents, "compSetPrimFloat", new FloatArrayValueDescriptor("primFloat", new float[] {123}));
        addPropertyValidator(startEvents, "compListWrapperedFloat", new ObjectArrayValueDescriptor("float", new Float[] {new Float(0), new Float(Float.MAX_VALUE), new Float(Float.MIN_VALUE), new Float(Float.POSITIVE_INFINITY), new Float(Float.NEGATIVE_INFINITY)}));
        addPropertyValidator(startEvents, "compSetWrapperedFloat", new ObjectArrayValueDescriptor("float", new Float[] {new Float(123)}));

        // int conversion
        addPropertyValidator(startEvents, "compListPrimInteger", new IntArrayValueDescriptor("primInt", new int[] {0, Integer.MAX_VALUE, Integer.MIN_VALUE}));
        addPropertyValidator(startEvents, "compSetPrimInteger", new IntArrayValueDescriptor("primInt", new int[] {123}));
        addPropertyValidator(startEvents, "compListWrapperedInteger", new ObjectArrayValueDescriptor("int", new Integer[] {new Integer((int)0), new Integer(Integer.MAX_VALUE), new Integer(Integer.MIN_VALUE)}));
        addPropertyValidator(startEvents, "compSetWrapperedInteger", new ObjectArrayValueDescriptor("int", new Integer[] {new Integer((int)123)}));

        // char conversion
        addPropertyValidator(startEvents, "compListPrimCharacter", new CharArrayValueDescriptor("primChar", new char[] {'\u0000', Character.MAX_VALUE}));
        addPropertyValidator(startEvents, "compSetPrimCharacter", new CharArrayValueDescriptor("primChar", new char[] {'A'}));
        addPropertyValidator(startEvents, "compListWrapperedCharacter", new ObjectArrayValueDescriptor("char", new Character[] {new Character('\u0000'), new Character(Character.MAX_VALUE)}));
        addPropertyValidator(startEvents, "compSetWrapperedCharacter", new ObjectArrayValueDescriptor("char", new Character[] {new Character('1')}));

        // short conversion
        addPropertyValidator(startEvents, "compListPrimShort", new ShortArrayValueDescriptor("primShort", new short[] {0, Short.MAX_VALUE, Short.MIN_VALUE}));
        addPropertyValidator(startEvents, "compSetPrimShort", new ShortArrayValueDescriptor("primShort", new short[] {123}));
        addPropertyValidator(startEvents, "compListWrapperedShort", new ObjectArrayValueDescriptor("short", new Short[] {new Short((short)0), new Short(Short.MAX_VALUE), new Short(Short.MIN_VALUE)}));
        addPropertyValidator(startEvents, "compSetWrapperedShort", new ObjectArrayValueDescriptor("short", new Short[] {new Short((short)123)}));

        // long conversion
        addPropertyValidator(startEvents, "compListPrimLong", new LongArrayValueDescriptor("primLong", new long[] {0, Long.MAX_VALUE, Long.MIN_VALUE}));
        addPropertyValidator(startEvents, "compSetPrimLong", new LongArrayValueDescriptor("primLong", new long[] {123}));
        addPropertyValidator(startEvents, "compListWrapperedLong", new ObjectArrayValueDescriptor("long", new Long[] {new Long((long)0), new Long(Long.MAX_VALUE), new Long(Long.MIN_VALUE)}));
        addPropertyValidator(startEvents, "compSetWrapperedLong", new ObjectArrayValueDescriptor("long", new Long[] {new Long((long)123)}));

        controller.run();
    }
}

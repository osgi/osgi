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

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.test.cases.blueprint.framework.ArgumentMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestArrayValue;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.framework.TestValue;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test the blueprint service as to injecting array as parameter (of constructor, property).
 *
 *
 */
public class TestArrayArgInjection extends DefaultTestBundleControl {
    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value, Class type) {
        // Check whether the correct parameter is set (whether a parameter with an index, 'arg1', has been set)
        startEvents.validateComponentArgument(id, "arg1", value, type);
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName,
            Object propertyValue, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
    }

    public void testArrayArgConstructor() throws Exception {
        // Load the bundle.
        StandardTestController controller = new StandardTestController(getContext(), this.getWebServer()
                + "www/array_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // Empty array
        Object[] expected = new String[0];
        this.addConstructorValidator(startEvents, "compEmptyArray", expected, expected.getClass());

        // Null array
        expected = new String[1];
        this.addConstructorValidator(startEvents, "compNullArray", expected, expected.getClass());

        // Single item array
        int[] expectedIntArr = new int[1];
        expectedIntArr[0] = 1;
        this.addConstructorValidator(startEvents, "compSinglePrimIntItem", expectedIntArr, expectedIntArr.getClass());

        // validate the metadata for this one too.  The embedded <value> metadata should inherit the
        // collection valueType.
        startEvents.addValidator(new ArgumentMetadataValidator("compSinglePrimIntItem", new TestArgument(
            new TestArrayValue(new TestValue[] { new TestStringValue(Integer.TYPE, "1") }, Integer.TYPE), "int[]", -1)));

        // Primitive int array
        expectedIntArr = new int[2];
        expectedIntArr[0] = 1;
        expectedIntArr[1] = 2;
        this.addConstructorValidator(startEvents, "compPrimIntArray", expectedIntArr, expectedIntArr.getClass());
        // Primitive int array created via implicit convertion
        this.addConstructorValidator(startEvents, "compImplicitPrimIntArray", expectedIntArr, expectedIntArr.getClass());


        // Wrapped int array
        expected = new Integer[2];
        expected[0] = new Integer(1);
        expected[1] = new Integer(2);
        this.addConstructorValidator(startEvents, "compWrappedIntArray", expected, expected.getClass());

        // Primitive boolean array
        boolean[] expectedBoolArr = new boolean[2];
        expectedBoolArr[0] = true;
        expectedBoolArr[1] = false;
        this.addConstructorValidator(startEvents, "compPrimBoolArray", expectedBoolArr, expectedBoolArr.getClass());

        // Wrapped boolean array
        expected = new Boolean[2];
        expected[0] = Boolean.TRUE;
        expected[1] = Boolean.FALSE;
        this.addConstructorValidator(startEvents, "compWrappedBoolArray", expected, expected.getClass());

        // String array
        expected = new String[2];
        expected[0] = "abc";
        expected[1] = "def";
        this.addConstructorValidator(startEvents, "compStringArray", expected, expected.getClass());

        // String array
        expected = new String[2];
        expected[0] = "abc";
        expected[1] = "def";
        this.addConstructorValidator(startEvents, "compStringArray", expected, expected.getClass());

        // Primitive byte array
        byte[] expectedByteArr = new byte[2];
        expectedByteArr[0] = 1;
        expectedByteArr[1] = 2;
        this.addConstructorValidator(startEvents, "compPrimByteArray", expectedByteArr, expectedByteArr.getClass());

        // Wrapped byte array
        expected = new Byte[2];
        expected[0] = new Byte((byte) 1);
        expected[1] = new Byte((byte) 2);
        this.addConstructorValidator(startEvents, "compWrappedByteArray", expected, expected.getClass());

        // Primitive char array
        char[] expectedCharArr = new char[2];
        expectedCharArr[0] = 'a';
        expectedCharArr[1] = 'b';
        this.addConstructorValidator(startEvents, "compCharArray", expectedCharArr, expectedCharArr.getClass());

        // Wrapped char array
        expected = new Character[2];
        expected[0] = new Character('a');
        expected[1] = new Character('b');
        this.addConstructorValidator(startEvents, "compWrappedCharArray", expected, expected.getClass());

        // Primitive short array
        short[] expectedShortArr = new short[2];
        expectedShortArr[0] = 3;
        expectedShortArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimShortArray", expectedShortArr, expectedShortArr.getClass());

        // Wrapped short array
        expected = new Short[2];
        expected[0] = new Short((short) 3);
        expected[1] = new Short((short) 4);
        this.addConstructorValidator(startEvents, "compWrappedShortArray", expected, expected.getClass());

        // Primitive long array
        long[] expectedLongArr = new long[2];
        expectedLongArr[0] = 3;
        expectedLongArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimLongArray", expectedLongArr, expectedLongArr.getClass());

        // Wrapped long array
        expected = new Long[2];
        expected[0] = new Long(3);
        expected[1] = new Long(4);
        this.addConstructorValidator(startEvents, "compWrappedLongArray", expected, expected.getClass());

        // Primitive double array
        double[] expectedDoubleArr = new double[2];
        expectedDoubleArr[0] = 3;
        expectedDoubleArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimDoubleArray", expectedDoubleArr, expectedDoubleArr
                .getClass());

        // Wrapped double array
        expected = new Double[2];
        expected[0] = new Double(3);
        expected[1] = new Double(4);
        this.addConstructorValidator(startEvents, "compWrappedDoubleArray", expected, expected.getClass());

        // Primitive float array
        float[] expectedFloatArr = new float[2];
        expectedFloatArr[0] = 3;
        expectedFloatArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimFloatArray", expectedFloatArr, expectedFloatArr.getClass());

        // Wrapped float array
        expected = new Float[2];
        expected[0] = new Float(3);
        expected[1] = new Float(4);
        this.addConstructorValidator(startEvents, "compWrappedFloatArray", expected, expected.getClass());

        // Date array
        expected = new Date[2];
        expected[0] = new GregorianCalendar(2009, 2, 19).getTime();
        expected[1] = new GregorianCalendar(2009, 2, 20).getTime();
        this.addConstructorValidator(startEvents, "compDateArray", expected, expected.getClass());

        // URL array
        expected = new URL[2];
        expected[0] = new URL("http://www.osgi.org");
        expected[1] = new URL("http://www.gmail.com");
        this.addConstructorValidator(startEvents, "compURLArray", expected, expected.getClass());

        // Class array
        expected = new Class[2];
        expected[0] = String.class;
        expected[1] = Boolean.class;
        this.addConstructorValidator(startEvents, "compClassArray", expected, expected.getClass());

        // Locale array
        expected = new Locale[2];
        expected[0] = new Locale("en", "US");
        expected[1] = new Locale("zh", "CN");
        this.addConstructorValidator(startEvents, "compLocaleArray", expected, expected.getClass());

        // Nested props
        Properties innerProps = new Properties();
        innerProps.put("administrator", "administrator@example.org");
        innerProps.put("support", "support@example.org");
        expected = new Properties[1];
        expected[0] = innerProps;
        this.addConstructorValidator(startEvents, "compNestedProps", expected, expected.getClass());

        // Nested map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new Map[1];
        expected[0] = innerMap;
        this.addConstructorValidator(startEvents, "compNestedMap", expected, expected.getClass());

        // Nested set
        Set innerSet = new HashSet();
        innerSet.add("administrator@example.org");
        innerSet.add("support@example.org");
        expected = new Set[1];
        expected[0] = innerSet;
        this.addConstructorValidator(startEvents, "compNestedSet", expected, expected.getClass());

        // Nested list
        List innerList = new ArrayList();
        innerList.add("administrator@example.org");
        innerList.add("support@example.org");
        expected = new List[1];
        expected[0] = innerList;
        this.addConstructorValidator(startEvents, "compNestedList", expected, expected.getClass());

        controller.run();
    }

    public void testArrayArgStaticFactoryConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), this.getWebServer()
                + "www/array_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // Empty array
        Object[] expected = new String[0];
        this.addConstructorValidator(startEvents, "compEmptyArray", expected, expected.getClass());

        // Null array
        expected = new String[1];
        this.addConstructorValidator(startEvents, "compNullArray", expected, String[].class);

        // String array
        expected = new String[2];
        expected[0] = "abc";
        expected[1] = "def";
        this.addConstructorValidator(startEvents, "compStringArray", expected, expected.getClass());

        // Primitive int array
        int[] expectedIntArr = new int[2];
        expectedIntArr[0] = 1;
        expectedIntArr[1] = 2;
        this.addConstructorValidator(startEvents, "compPrimIntArray", expectedIntArr, expectedIntArr.getClass());

        // Wrapped int array
        expected = new Integer[2];
        expected[0] = new Integer(1);
        expected[1] = new Integer(2);
        this.addConstructorValidator(startEvents, "compWrappedIntArray", expected, expected.getClass());

        // Primitive boolean array
        boolean[] expectedBoolArr = new boolean[2];
        expectedBoolArr[0] = true;
        expectedBoolArr[1] = false;
        this.addConstructorValidator(startEvents, "compPrimBoolArray", expectedBoolArr, expectedBoolArr
                .getClass());

        // Wrapped boolean array
        expected = new Boolean[2];
        expected[0] = Boolean.TRUE;
        expected[1] = Boolean.FALSE;
        this.addConstructorValidator(startEvents, "compWrappedBooleanArray", expected, expected.getClass());

        // Primitive byte array
        byte[] expectedByteArr = new byte[2];
        expectedByteArr[0] = 1;
        expectedByteArr[1] = 2;
        this.addConstructorValidator(startEvents, "compPrimByteArray", expectedByteArr, expectedByteArr
                .getClass());

        // Wrapped byte array
        expected = new Byte[2];
        expected[0] = new Byte((byte) 1);
        expected[1] = new Byte((byte) 2);
        this.addConstructorValidator(startEvents, "compWrappedByteArray", expected, expected.getClass());

        // Primitive char array
        char[] expectedCharArr = new char[2];
        expectedCharArr[0] = 'a';
        expectedCharArr[1] = 'b';
        this.addConstructorValidator(startEvents, "compPrimCharArray", expectedCharArr, expectedCharArr
                .getClass());

        // Wrapped char array
        expected = new Character[2];
        expected[0] = new Character('a');
        expected[1] = new Character('b');
        this.addConstructorValidator(startEvents, "compWrappedCharArray", expected, expected.getClass());

        // Primitive short array
        short[] expectedShortArr = new short[2];
        expectedShortArr[0] = 3;
        expectedShortArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimShortArray", expectedShortArr, expectedShortArr
                .getClass());

        // Wrapped short array
        expected = new Short[2];
        expected[0] = new Short((short) 3);
        expected[1] = new Short((short) 4);
        this.addConstructorValidator(startEvents, "compWrappedShortArray", expected, expected.getClass());

        // Primitive long array
        long[] expectedLongArr = new long[2];
        expectedLongArr[0] = 3;
        expectedLongArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimLongArray", expectedLongArr, expectedLongArr
                .getClass());

        // Wrapped long array
        expected = new Long[2];
        expected[0] = new Long(3);
        expected[1] = new Long(4);
        this.addConstructorValidator(startEvents, "compWrappedLongArray", expected, expected.getClass());

        // Primitive double array
        double[] expectedDoubleArr = new double[2];
        expectedDoubleArr[0] = 3;
        expectedDoubleArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimDoubleArray", expectedDoubleArr, expectedDoubleArr
                .getClass());

        // Wrapped double array
        expected = new Double[2];
        expected[0] = new Double(3);
        expected[1] = new Double(4);
        this.addConstructorValidator(startEvents, "compWrappedDoubleArray", expected, expected.getClass());

        // Primitive float array
        float[] expectedFloatArr = new float[2];
        expectedFloatArr[0] = 3;
        expectedFloatArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimFloatArray", expectedFloatArr, expectedFloatArr
                .getClass());

        // Wrapped float array
        expected = new Float[2];
        expected[0] = new Float(3);
        expected[1] = new Float(4);
        this.addConstructorValidator(startEvents, "compWrappedFloatArray", expected, expected.getClass());

        // Date array
        expected = new Date[2];
        expected[0] = new GregorianCalendar(2009, 2, 19).getTime();
        expected[1] = new GregorianCalendar(2009, 2, 20).getTime();
        this.addConstructorValidator(startEvents, "compDateArray", expected, expected.getClass());

        // URL array
        expected = new URL[2];
        expected[0] = new URL("http://www.osgi.org");
        expected[1] = new URL("http://www.gmail.com");
        this.addConstructorValidator(startEvents, "compURLArray", expected, expected.getClass());

        // Class array
        expected = new Class[2];
        expected[0] = String.class;
        expected[1] = Boolean.class;
        this.addConstructorValidator(startEvents, "compClassArray", expected, expected.getClass());

        // Locale array
        expected = new Locale[2];
        expected[0] = new Locale("en", "US");
        expected[1] = new Locale("zh", "CN");
        this.addConstructorValidator(startEvents, "compLocaleArray", expected, expected.getClass());

        // Nested props
        Properties innerProps = new Properties();
        innerProps.put("administrator", "administrator@example.org");
        innerProps.put("support", "support@example.org");
        expected = new Properties[1];
        expected[0] = innerProps;
        this.addConstructorValidator(startEvents, "compNestedProps", expected, expected.getClass());

        // Nested map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new Map[1];
        expected[0] = innerMap;
        this.addConstructorValidator(startEvents, "compNestedMap", expected, expected.getClass());

        // Nested set
        Set innerSet = new HashSet();
        innerSet.add("administrator@example.org");
        innerSet.add("support@example.org");
        expected = new Set[1];
        expected[0] = innerSet;
        this.addConstructorValidator(startEvents, "compNestedSet", expected, expected.getClass());

        // Nested list
        List innerList = new ArrayList();
        innerList.add("administrator@example.org");
        innerList.add("support@example.org");
        expected = new List[1];
        expected[0] = innerList;
        this.addConstructorValidator(startEvents, "compNestedList", expected, expected.getClass());

        // Nested array
        String [][] nested = new String[1][2];
        nested[0][0] = "abc";
        nested[0][1] = "def";
        this.addConstructorValidator(startEvents, "compNestedArray", nested, nested.getClass());

        controller.run();
    }

    public void testArrayArgInstanceFactoryConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/array_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // Empty array
        Object[] expected = new String[0];
        this.addConstructorValidator(startEvents, "compEmptyArray", expected, expected.getClass());

        // Null array
        expected = new String[1];
        this.addConstructorValidator(startEvents, "compNullArray", expected, String[].class);

        // String array
        expected = new String[2];
        expected[0] = "abc";
        expected[1] = "def";
        this.addConstructorValidator(startEvents, "compStringArray", expected, expected
                .getClass());

        // Primitive int array
        int[] expectedIntArr = new int[2];
        expectedIntArr[0] = 1;
        expectedIntArr[1] = 2;
        this.addConstructorValidator(startEvents, "compPrimIntArray", expectedIntArr,
                expectedIntArr.getClass());

        // Wrapped int array
        expected = new Integer[2];
        expected[0] = new Integer(1);
        expected[1] = new Integer(2);
        this.addConstructorValidator(startEvents, "compWrappedIntArray", expected, expected
                .getClass());

        // Primitive boolean array
        boolean[] expectedBoolArr = new boolean[2];
        expectedBoolArr[0] = true;
        expectedBoolArr[1] = false;
        this.addConstructorValidator(startEvents, "compPrimBoolArray", expectedBoolArr,
                expectedBoolArr.getClass());

        // Wrapped boolean array
        expected = new Boolean[2];
        expected[0] = Boolean.TRUE;
        expected[1] = Boolean.FALSE;
        this.addConstructorValidator(startEvents, "compWrappedBooleanArray", expected, expected
                .getClass());

        // Primitive byte array
        byte[] expectedByteArr = new byte[2];
        expectedByteArr[0] = 1;
        expectedByteArr[1] = 2;
        this.addConstructorValidator(startEvents, "compPrimByteArray", expectedByteArr,
                expectedByteArr.getClass());

        // Wrapped byte array
        expected = new Byte[2];
        expected[0] = new Byte((byte) 1);
        expected[1] = new Byte((byte) 2);
        this.addConstructorValidator(startEvents, "compWrappedByteArray", expected, expected
                .getClass());

        // Primitive char array
        char[] expectedCharArr = new char[2];
        expectedCharArr[0] = 'a';
        expectedCharArr[1] = 'b';
        this.addConstructorValidator(startEvents, "compPrimCharArray", expectedCharArr,
                expectedCharArr.getClass());

        // Wrapped char array
        expected = new Character[2];
        expected[0] = new Character('a');
        expected[1] = new Character('b');
        this.addConstructorValidator(startEvents, "compWrappedCharArray", expected, expected
                .getClass());

        // Primitive short array
        short[] expectedShortArr = new short[2];
        expectedShortArr[0] = 3;
        expectedShortArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimShortArray", expectedShortArr,
                expectedShortArr.getClass());

        // Wrapped short array
        expected = new Short[2];
        expected[0] = new Short((short) 3);
        expected[1] = new Short((short) 4);
        this.addConstructorValidator(startEvents, "compWrappedShortArray", expected, expected
                .getClass());

        // Primitive long array
        long[] expectedLongArr = new long[2];
        expectedLongArr[0] = 3;
        expectedLongArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimLongArray", expectedLongArr,
                expectedLongArr.getClass());

        // Wrapped long array
        expected = new Long[2];
        expected[0] = new Long(3);
        expected[1] = new Long(4);
        this.addConstructorValidator(startEvents, "compWrappedLongArray", expected, expected
                .getClass());

        // Primitive double array
        double[] expectedDoubleArr = new double[2];
        expectedDoubleArr[0] = 3;
        expectedDoubleArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimDoubleArray", expectedDoubleArr,
                expectedDoubleArr.getClass());

        // Wrapped double array
        expected = new Double[2];
        expected[0] = new Double(3);
        expected[1] = new Double(4);
        this.addConstructorValidator(startEvents, "compWrappedDoubleArray", expected, expected
                .getClass());

        // Primitive float array
        float[] expectedFloatArr = new float[2];
        expectedFloatArr[0] = 3;
        expectedFloatArr[1] = 4;
        this.addConstructorValidator(startEvents, "compPrimFloatArray", expectedFloatArr,
                expectedFloatArr.getClass());

        // Wrapped float array
        expected = new Float[2];
        expected[0] = new Float(3);
        expected[1] = new Float(4);
        this.addConstructorValidator(startEvents, "compWrappedFloatArray", expected, expected
                .getClass());

        // Date array
        expected = new Date[2];
        expected[0] = new GregorianCalendar(2009, 2, 19).getTime();
        expected[1] = new GregorianCalendar(2009, 2, 20).getTime();
        this.addConstructorValidator(startEvents, "compDateArray", expected, expected.getClass());

        // URL array
        expected = new URL[2];
        expected[0] = new URL("http://www.osgi.org");
        expected[1] = new URL("http://www.gmail.com");
        this.addConstructorValidator(startEvents, "compURLArray", expected, expected.getClass());

        // Class array
        expected = new Class[2];
        expected[0] = String.class;
        expected[1] = Boolean.class;
        this.addConstructorValidator(startEvents, "compClassArray", expected, expected.getClass());

        // Locale array
        expected = new Locale[2];
        expected[0] = new Locale("en", "US");
        expected[1] = new Locale("zh", "CN");
        this.addConstructorValidator(startEvents, "compLocaleArray", expected, expected.getClass());

        // Nested props
        Properties innerProps = new Properties();
        innerProps.put("administrator", "administrator@example.org");
        innerProps.put("support", "support@example.org");
        expected = new Properties[1];
        expected[0] = innerProps;
        this.addConstructorValidator(startEvents, "compNestedProps", expected, expected.getClass());

        // Nested map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new Map[1];
        expected[0] = innerMap;
        this.addConstructorValidator(startEvents, "compNestedMap", expected, expected.getClass());

        // Nested set
        Set innerSet = new HashSet();
        innerSet.add("administrator@example.org");
        innerSet.add("support@example.org");
        expected = new Set[1];
        expected[0] = innerSet;
        this.addConstructorValidator(startEvents, "compNestedSet", expected, expected.getClass());

        // Nested list
        List innerList = new ArrayList();
        innerList.add("administrator@example.org");
        innerList.add("support@example.org");
        expected = new List[1];
        expected[0] = innerList;
        this.addConstructorValidator(startEvents, "compNestedList", expected, expected.getClass());

        // Nested array
        String [][] nested = new String[1][2];
        nested[0][0] = "abc";
        nested[0][1] = "def";
        this.addConstructorValidator(startEvents, "compNestedArray", nested, nested.getClass());
        controller.run();
    }

    public void testArrayArgProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/array_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // Empty array
        Object[] expected = new Object[0];
        addPropertyValidator(startEvents, "compEmptyArray", "emptyArray", expected, expected.getClass());

        // Null array
        expected = new Object[1];
        addPropertyValidator(startEvents, "compNullArray", "nullArray", expected, Object[].class);

        // String array
        expected = new String[2];
        expected[0] = "abc";
        expected[1] = "def";
        addPropertyValidator(startEvents, "compStringArray", "stringArray", expected, expected.getClass());

        // Primitive int array
        int[] expectedIntArr = new int[2];
        expectedIntArr[0] = 1;
        expectedIntArr[1] = 2;
        addPropertyValidator(startEvents, "compPrimIntArray", "primIntArray", expectedIntArr, expectedIntArr.getClass());

        // Wrapped int array
        expected = new Integer[2];
        expected[0] = new Integer(1);
        expected[1] = new Integer(2);
        addPropertyValidator(startEvents, "compWrappedIntArray", "wrappedIntArray", expected, expected.getClass());

        // Primitive boolean array
        boolean[] expectedBoolArr = new boolean[2];
        expectedBoolArr[0] = true;
        expectedBoolArr[1] = false;
        addPropertyValidator(startEvents, "compPrimBoolArray", "primBoolArray", expectedBoolArr, expectedBoolArr
                .getClass());

        // Wrapped boolean array
        expected = new Boolean[2];
        expected[0] = Boolean.TRUE;
        expected[1] = Boolean.FALSE;
        addPropertyValidator(startEvents, "compWrappedBooleanArray", "wrappedBooleanArray", expected, expected
                .getClass());

        // Primitive byte array
        byte[] expectedByteArr = new byte[2];
        expectedByteArr[0] = 1;
        expectedByteArr[1] = 2;
        addPropertyValidator(startEvents, "compPrimByteArray", "primByteArray", expectedByteArr, expectedByteArr
                .getClass());

        // Wrapped byte array
        expected = new Byte[2];
        expected[0] = new Byte((byte) 1);
        expected[1] = new Byte((byte) 2);
        addPropertyValidator(startEvents, "compWrappedByteArray", "wrappedByteArray", expected, expected.getClass());

        // Primitive char array
        char[] expectedCharArr = new char[2];
        expectedCharArr[0] = 'a';
        expectedCharArr[1] = 'b';
        addPropertyValidator(startEvents, "compPrimCharArray", "primCharArray", expectedCharArr, expectedCharArr
                .getClass());

        // Wrapped char array
        expected = new Character[2];
        expected[0] = new Character('a');
        expected[1] = new Character('b');
        addPropertyValidator(startEvents, "compWrappedCharArray", "wrappedCharArray", expected, expected.getClass());

        // Primitive short array
        short[] expectedShortArr = new short[2];
        expectedShortArr[0] = 3;
        expectedShortArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimShortArray", "primShortArray", expectedShortArr, expectedShortArr
                .getClass());

        // Wrapped short array
        expected = new Short[2];
        expected[0] = new Short((short) 3);
        expected[1] = new Short((short) 4);
        addPropertyValidator(startEvents, "compWrappedShortArray", "wrappedShortArray", expected, expected.getClass());

        // Primitive long array
        long[] expectedLongArr = new long[2];
        expectedLongArr[0] = 3;
        expectedLongArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimLongArray", "primLongArray", expectedLongArr, expectedLongArr
                .getClass());

        // Wrapped long array
        expected = new Long[2];
        expected[0] = new Long(3);
        expected[1] = new Long(4);
        addPropertyValidator(startEvents, "compWrappedLongArray", "wrappedLongArray", expected, expected.getClass());

        // Primitive double array
        double[] expectedDoubleArr = new double[2];
        expectedDoubleArr[0] = 3;
        expectedDoubleArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimDoubleArray", "primDoubleArray", expectedDoubleArr,
                expectedDoubleArr.getClass());

        // Wrapped double array
        expected = new Double[2];
        expected[0] = new Double(3);
        expected[1] = new Double(4);
        addPropertyValidator(startEvents, "compWrappedDoubleArray", "wrappedDoubleArray", expected, expected.getClass());

        // Primitive float array
        float[] expectedFloatArr = new float[2];
        expectedFloatArr[0] = 3;
        expectedFloatArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimFloatArray", "primFloatArray", expectedFloatArr, expectedFloatArr
                .getClass());

        // Wrapped float array
        expected = new Float[2];
        expected[0] = new Float(3);
        expected[1] = new Float(4);
        addPropertyValidator(startEvents, "compWrappedFloatArray", "wrappedFloatArray", expected, expected.getClass());

        // Date array
        expected = new Date[2];
        expected[0] = new GregorianCalendar(2009, 2, 19).getTime();
        expected[1] = new GregorianCalendar(2009, 2, 20).getTime();
        addPropertyValidator(startEvents, "compDateArray", "dateArray", expected, expected.getClass());

        // URL array
        expected = new URL[2];
        expected[0] = new URL("http://www.osgi.org");
        expected[1] = new URL("http://www.gmail.com");
        addPropertyValidator(startEvents, "compUrlArray", "urlArray", expected, expected.getClass());

        // Class array
        expected = new Class[2];
        expected[0] = String.class;
        expected[1] = Boolean.class;
        addPropertyValidator(startEvents, "compClassArray", "classArray", expected, expected.getClass());

        // Locale array
        expected = new Locale[2];
        expected[0] = new Locale("en", "US");
        expected[1] = new Locale("zh", "CN");
        addPropertyValidator(startEvents, "compLocaleArray", "localeArray", expected, expected.getClass());

        // Nested props
        Properties innerProps = new Properties();
        innerProps.put("administrator", "administrator@example.org");
        innerProps.put("support", "support@example.org");
        expected = new Properties[1];
        expected[0] = innerProps;
        addPropertyValidator(startEvents, "compNestedProps", "propsArray", expected, expected.getClass());

        // Nested map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new Map[1];
        expected[0] = innerMap;
        addPropertyValidator(startEvents, "compNestedMap", "mapArray", expected, expected.getClass());

        // Nested set
        Set innerSet = new HashSet();
        innerSet.add("administrator@example.org");
        innerSet.add("support@example.org");
        expected = new Set[1];
        expected[0] = innerSet;
        addPropertyValidator(startEvents, "compNestedSet", "setArray", expected, expected.getClass());

        // Nested list
        List innerList = new ArrayList();
        innerList.add("administrator@example.org");
        innerList.add("support@example.org");
        expected = new List[1];
        expected[0] = innerList;
        addPropertyValidator(startEvents, "compNestedList", "listArray", expected, expected.getClass());

        // Nested array
        String[] innerArr = new String[2];
        innerArr[0] = "abc";
        innerArr[1] = "def";
        expected = new Object[1];
        expected[0] = innerArr;
        addPropertyValidator(startEvents, "compNestedArray", "nestedArray", expected, expected.getClass());

        controller.run();
    }

    public void testStaticFactoryArrayArgProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/array_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // Empty array
        Object[] expected = new Object[0];
        addPropertyValidator(startEvents, "compEmptyArray", "emptyArray", expected, expected.getClass());

        // Null array
        expected = new Object[1];
        addPropertyValidator(startEvents, "compNullArray", "nullArray", expected, Object[].class);

        // String array
        expected = new String[2];
        expected[0] = "abc";
        expected[1] = "def";
        addPropertyValidator(startEvents, "compStringArray", "stringArray", expected, expected.getClass());

        // Primitive int array
        int[] expectedIntArr = new int[2];
        expectedIntArr[0] = 1;
        expectedIntArr[1] = 2;
        addPropertyValidator(startEvents, "compPrimIntArray", "primIntArray", expectedIntArr, expectedIntArr.getClass());

        // Wrapped int array
        expected = new Integer[2];
        expected[0] = new Integer(1);
        expected[1] = new Integer(2);
        addPropertyValidator(startEvents, "compWrappedIntArray", "wrappedIntArray", expected, expected.getClass());

        // Primitive boolean array
        boolean[] expectedBoolArr = new boolean[2];
        expectedBoolArr[0] = true;
        expectedBoolArr[1] = false;
        addPropertyValidator(startEvents, "compPrimBoolArray", "primBoolArray", expectedBoolArr, expectedBoolArr
                .getClass());

        // Wrapped boolean array
        expected = new Boolean[2];
        expected[0] = Boolean.TRUE;
        expected[1] = Boolean.FALSE;
        addPropertyValidator(startEvents, "compWrappedBooleanArray", "wrappedBooleanArray", expected, expected
                .getClass());

        // Primitive byte array
        byte[] expectedByteArr = new byte[2];
        expectedByteArr[0] = 1;
        expectedByteArr[1] = 2;
        addPropertyValidator(startEvents, "compPrimByteArray", "primByteArray", expectedByteArr, expectedByteArr
                .getClass());

        // Wrapped byte array
        expected = new Byte[2];
        expected[0] = new Byte((byte) 1);
        expected[1] = new Byte((byte) 2);
        addPropertyValidator(startEvents, "compWrappedByteArray", "wrappedByteArray", expected, expected.getClass());

        // Primitive char array
        char[] expectedCharArr = new char[2];
        expectedCharArr[0] = 'a';
        expectedCharArr[1] = 'b';
        addPropertyValidator(startEvents, "compPrimCharArray", "primCharArray", expectedCharArr, expectedCharArr
                .getClass());

        // Wrapped char array
        expected = new Character[2];
        expected[0] = new Character('a');
        expected[1] = new Character('b');
        addPropertyValidator(startEvents, "compWrappedCharArray", "wrappedCharArray", expected, expected.getClass());

        // Primitive short array
        short[] expectedShortArr = new short[2];
        expectedShortArr[0] = 3;
        expectedShortArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimShortArray", "primShortArray", expectedShortArr, expectedShortArr
                .getClass());

        // Wrapped short array
        expected = new Short[2];
        expected[0] = new Short((short) 3);
        expected[1] = new Short((short) 4);
        addPropertyValidator(startEvents, "compWrappedShortArray", "wrappedShortArray", expected, expected.getClass());

        // Primitive long array
        long[] expectedLongArr = new long[2];
        expectedLongArr[0] = 3;
        expectedLongArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimLongArray", "primLongArray", expectedLongArr, expectedLongArr
                .getClass());

        // Wrapped long array
        expected = new Long[2];
        expected[0] = new Long(3);
        expected[1] = new Long(4);
        addPropertyValidator(startEvents, "compWrappedLongArray", "wrappedLongArray", expected, expected.getClass());

        // Primitive double array
        double[] expectedDoubleArr = new double[2];
        expectedDoubleArr[0] = 3;
        expectedDoubleArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimDoubleArray", "primDoubleArray", expectedDoubleArr,
                expectedDoubleArr.getClass());

        // Wrapped double array
        expected = new Double[2];
        expected[0] = new Double(3);
        expected[1] = new Double(4);
        addPropertyValidator(startEvents, "compWrappedDoubleArray", "wrappedDoubleArray", expected, expected.getClass());

        // Primitive float array
        float[] expectedFloatArr = new float[2];
        expectedFloatArr[0] = 3;
        expectedFloatArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimFloatArray", "primFloatArray", expectedFloatArr, expectedFloatArr
                .getClass());

        // Wrapped float array
        expected = new Float[2];
        expected[0] = new Float(3);
        expected[1] = new Float(4);
        addPropertyValidator(startEvents, "compWrappedFloatArray", "wrappedFloatArray", expected, expected.getClass());

        // Date array
        expected = new Date[2];
        expected[0] = new GregorianCalendar(2009, 2, 19).getTime();
        expected[1] = new GregorianCalendar(2009, 2, 20).getTime();
        addPropertyValidator(startEvents, "compDateArray", "dateArray", expected, expected.getClass());

        // URL array
        expected = new URL[2];
        expected[0] = new URL("http://www.osgi.org");
        expected[1] = new URL("http://www.gmail.com");
        addPropertyValidator(startEvents, "compUrlArray", "urlArray", expected, expected.getClass());

        // Class array
        expected = new Class[2];
        expected[0] = String.class;
        expected[1] = Boolean.class;
        addPropertyValidator(startEvents, "compClassArray", "classArray", expected, expected.getClass());

        // Locale array
        expected = new Locale[2];
        expected[0] = new Locale("en", "US");
        expected[1] = new Locale("zh", "CN");
        addPropertyValidator(startEvents, "compLocaleArray", "localeArray", expected, expected.getClass());

        // Nested props
        Properties innerProps = new Properties();
        innerProps.put("administrator", "administrator@example.org");
        innerProps.put("support", "support@example.org");
        expected = new Properties[1];
        expected[0] = innerProps;
        addPropertyValidator(startEvents, "compNestedProps", "propsArray", expected, expected.getClass());

        // Nested map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new Map[1];
        expected[0] = innerMap;
        addPropertyValidator(startEvents, "compNestedMap", "mapArray", expected, expected.getClass());

        // Nested set
        Set innerSet = new HashSet();
        innerSet.add("administrator@example.org");
        innerSet.add("support@example.org");
        expected = new Set[1];
        expected[0] = innerSet;
        addPropertyValidator(startEvents, "compNestedSet", "setArray", expected, expected.getClass());

        // Nested list
        List innerList = new ArrayList();
        innerList.add("administrator@example.org");
        innerList.add("support@example.org");
        expected = new List[1];
        expected[0] = innerList;
        addPropertyValidator(startEvents, "compNestedList", "listArray", expected, expected.getClass());

        // Nested array
        String[] innerArr = new String[2];
        innerArr[0] = "abc";
        innerArr[1] = "def";
        expected = new Object[1];
        expected[0] = innerArr;
        addPropertyValidator(startEvents, "compNestedArray", "nestedArray", expected, expected.getClass());

        controller.run();
    }

    public void testInstanceFactoryArrayArgProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/array_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // Empty array
        Object[] expected = new Object[0];
        addPropertyValidator(startEvents, "compEmptyArray", "emptyArray", expected, expected.getClass());

        // Null array
        expected = new Object[1];
        addPropertyValidator(startEvents, "compNullArray", "nullArray", expected, Object[].class);

        // String array
        expected = new String[2];
        expected[0] = "abc";
        expected[1] = "def";
        addPropertyValidator(startEvents, "compStringArray", "stringArray", expected, expected.getClass());

        // Primitive int array
        int[] expectedIntArr = new int[2];
        expectedIntArr[0] = 1;
        expectedIntArr[1] = 2;
        addPropertyValidator(startEvents, "compPrimIntArray", "primIntArray", expectedIntArr, expectedIntArr.getClass());

        // Wrapped int array
        expected = new Integer[2];
        expected[0] = new Integer(1);
        expected[1] = new Integer(2);
        addPropertyValidator(startEvents, "compWrappedIntArray", "wrappedIntArray", expected, expected.getClass());

        // Primitive boolean array
        boolean[] expectedBoolArr = new boolean[2];
        expectedBoolArr[0] = true;
        expectedBoolArr[1] = false;
        addPropertyValidator(startEvents, "compPrimBoolArray", "primBoolArray", expectedBoolArr, expectedBoolArr
                .getClass());

        // Wrapped boolean array
        expected = new Boolean[2];
        expected[0] = Boolean.TRUE;
        expected[1] = Boolean.FALSE;
        addPropertyValidator(startEvents, "compWrappedBooleanArray", "wrappedBooleanArray", expected, expected
                .getClass());

        // Primitive byte array
        byte[] expectedByteArr = new byte[2];
        expectedByteArr[0] = 1;
        expectedByteArr[1] = 2;
        addPropertyValidator(startEvents, "compPrimByteArray", "primByteArray", expectedByteArr, expectedByteArr
                .getClass());

        // Wrapped byte array
        expected = new Byte[2];
        expected[0] = new Byte((byte) 1);
        expected[1] = new Byte((byte) 2);
        addPropertyValidator(startEvents, "compWrappedByteArray", "wrappedByteArray", expected, expected.getClass());

        // Primitive char array
        char[] expectedCharArr = new char[2];
        expectedCharArr[0] = 'a';
        expectedCharArr[1] = 'b';
        addPropertyValidator(startEvents, "compPrimCharArray", "primCharArray", expectedCharArr, expectedCharArr
                .getClass());

        // Wrapped char array
        expected = new Character[2];
        expected[0] = new Character('a');
        expected[1] = new Character('b');
        addPropertyValidator(startEvents, "compWrappedCharArray", "wrappedCharArray", expected, expected.getClass());

        // Primitive short array
        short[] expectedShortArr = new short[2];
        expectedShortArr[0] = 3;
        expectedShortArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimShortArray", "primShortArray", expectedShortArr, expectedShortArr
                .getClass());

        // Wrapped short array
        expected = new Short[2];
        expected[0] = new Short((short) 3);
        expected[1] = new Short((short) 4);
        addPropertyValidator(startEvents, "compWrappedShortArray", "wrappedShortArray", expected, expected.getClass());

        // Primitive long array
        long[] expectedLongArr = new long[2];
        expectedLongArr[0] = 3;
        expectedLongArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimLongArray", "primLongArray", expectedLongArr, expectedLongArr
                .getClass());

        // Wrapped long array
        expected = new Long[2];
        expected[0] = new Long(3);
        expected[1] = new Long(4);
        addPropertyValidator(startEvents, "compWrappedLongArray", "wrappedLongArray", expected, expected.getClass());

        // Primitive double array
        double[] expectedDoubleArr = new double[2];
        expectedDoubleArr[0] = 3;
        expectedDoubleArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimDoubleArray", "primDoubleArray", expectedDoubleArr,
                expectedDoubleArr.getClass());

        // Wrapped double array
        expected = new Double[2];
        expected[0] = new Double(3);
        expected[1] = new Double(4);
        addPropertyValidator(startEvents, "compWrappedDoubleArray", "wrappedDoubleArray", expected, expected.getClass());

        // Primitive float array
        float[] expectedFloatArr = new float[2];
        expectedFloatArr[0] = 3;
        expectedFloatArr[1] = 4;
        addPropertyValidator(startEvents, "compPrimFloatArray", "primFloatArray", expectedFloatArr, expectedFloatArr
                .getClass());

        // Wrapped float array
        expected = new Float[2];
        expected[0] = new Float(3);
        expected[1] = new Float(4);
        addPropertyValidator(startEvents, "compWrappedFloatArray", "wrappedFloatArray", expected, expected.getClass());

        // Date array
        expected = new Date[2];
        expected[0] = new GregorianCalendar(2009, 2, 19).getTime();
        expected[1] = new GregorianCalendar(2009, 2, 20).getTime();
        addPropertyValidator(startEvents, "compDateArray", "dateArray", expected, expected.getClass());

        // URL array
        expected = new URL[2];
        expected[0] = new URL("http://www.osgi.org");
        expected[1] = new URL("http://www.gmail.com");
        addPropertyValidator(startEvents, "compUrlArray", "urlArray", expected, expected.getClass());

        // Class array
        expected = new Class[2];
        expected[0] = String.class;
        expected[1] = Boolean.class;
        addPropertyValidator(startEvents, "compClassArray", "classArray", expected, expected.getClass());

        // Locale array
        expected = new Locale[2];
        expected[0] = new Locale("en", "US");
        expected[1] = new Locale("zh", "CN");
        addPropertyValidator(startEvents, "compLocaleArray", "localeArray", expected, expected.getClass());

        // Nested props
        Properties innerProps = new Properties();
        innerProps.put("administrator", "administrator@example.org");
        innerProps.put("support", "support@example.org");
        expected = new Properties[1];
        expected[0] = innerProps;
        addPropertyValidator(startEvents, "compNestedProps", "propsArray", expected, expected.getClass());

        // Nested map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new Map[1];
        expected[0] = innerMap;
        addPropertyValidator(startEvents, "compNestedMap", "mapArray", expected, expected.getClass());

        // Nested set
        Set innerSet = new HashSet();
        innerSet.add("administrator@example.org");
        innerSet.add("support@example.org");
        expected = new Set[1];
        expected[0] = innerSet;
        addPropertyValidator(startEvents, "compNestedSet", "setArray", expected, expected.getClass());

        // Nested list
        List innerList = new ArrayList();
        innerList.add("administrator@example.org");
        innerList.add("support@example.org");
        expected = new List[1];
        expected[0] = innerList;
        addPropertyValidator(startEvents, "compNestedList", "listArray", expected, expected.getClass());

        // Nested array
        String[] innerArr = new String[2];
        innerArr[0] = "abc";
        innerArr[1] = "def";
        expected = new Object[1];
        expected[0] = innerArr;
        addPropertyValidator(startEvents, "compNestedArray", "nestedArray", expected, expected.getClass());

        controller.run();
    }
}

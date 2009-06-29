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

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Dictionary;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Locale;
import java.util.BitSet;
import java.util.regex.Pattern;

import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.AssertionService;

/**
 * Test component for validating conversion results for
 * different types when using the blueprintConverter
 * environment component.  This will do conversions for
 * the complete set of defined builtin conversions.
 */
public class ConversionServiceChecker extends BaseTestComponent {
    // our conversion service, injected into the constructor
    protected Converter blueprintConverter;

    public ConversionServiceChecker(String componentId, Converter blueprintConverter) {
        super(componentId);
        this.blueprintConverter = blueprintConverter;
    }

    /**
     * Perform the conversion service validations.
     */
    public void init() {
        // Conversion step 1:  assignability test, which does not require explicit conversion
        Object source = new AsianRegionCode("CN+86");
        AssertionService.assertSame(this, "Conversion service assignability test", source, convert(source, new ReifiedType(RegionCode.class)));
        // some simple tests for wrapper/primitive requests
        source = new Integer(1);
        // there might be some boxing/unboxing going on, so don't assume this is the same object.
        AssertionService.assertEquals(this, "Conversion service primitive type request", source, convert(source, new ReifiedType(Integer.TYPE)));
        AssertionService.assertEquals(this, "Conversion service wrapper type request", source, convert(source, new ReifiedType(Integer.class)));

        // Conversion step 2:  Try converters in declaration order.  We'll request a RegionCode, which should be handled by the AsianRegionCode converter.
        AssertionService.assertSame(this, "Conversion service assignability test", new AsianRegionCode("CN+86"), convert("CN+86", new ReifiedType(RegionCode.class)));
        // request the most specific type also
        AssertionService.assertSame(this, "Conversion service assignability test", new AsianRegionCode("CN+86"), convert("CN+86", new ReifiedType(AsianRegionCode.class)));

        // Conversion step 3:  The target is an array.  The source must be an array or a Collection and the elements must be type convertable.
        // String array to an int array
        Object target = new int[] { 1, 2 };
        source = new String[] { "1", "2" };
        Object converted = convert(source, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed array conversion", (int[])target, (int[])converted);
        // an unboxing conversion
        source = new Integer[] { new Integer(1), new Integer(2) };
        converted = convert(source, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed array conversion", (int[])target, (int[])converted);
        // and do the boxing in the other direction
        target = source;
        source = new int[] { 1, 2 };

        converted = convert(source, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed array conversion", (Integer[])target, (Integer[])converted);

        // an array conversion requiring type converter involvement
        target = new RegionCode[] { new AsianRegionCode("CN+86") };
        source = new String[] { "CN+86" };
        converted = convert(source, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed array conversion", (Object[])target, (Object[])converted);

        // repeat using a collection as the source
        target = new int[] { 1, 2 };
        List listSource = new ArrayList();
        listSource.add("1");
        listSource.add("2");

        converted = convert(listSource, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed collection to array conversion", (int[])target, (int[])converted);
        // an unboxing conversion
        listSource = new ArrayList();
        listSource.add(new Integer(1));
        listSource.add(new Integer(2));
        converted = convert(source, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed collection to array conversion", (int[])target, (int[])converted);
        // and do the boxing in the other direction
        target = new Integer[] { new Integer(1), new Integer(2) };

        converted = convert(listSource, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed collection to array conversion", (Object[])target, (Object[])converted);

        // an array conversion requiring type converter involvement
        target = new RegionCode[] { new AsianRegionCode("CN+86") };
        listSource = new ArrayList();
        listSource.add("CN+86");
        converted = convert(listSource, new ReifiedType(target.getClass()));
        AssertionService.assertArrayEquals(this, "Failed collection to array conversion", (Object[])target, (Object[])converted);

        // pseudo conversion...convert an array of List to a generic list that requires element conversion

        source = new List[] { listSource };
        List targetList = new ArrayList();
        targetList.add(new AsianRegionCode("CN+86"));
        target = new List[] { targetList };

        converted = convert(listSource, new CheckerReifiedType(List[].class, new Class[] { RegionCode.class }));
        AssertionService.assertArrayEquals(this, "Failed collection to array conversion", (Object[])target, (Object[])converted);

        // Conversion step 4:  The target implements collection.  The source must also be an array or collection.

        // we're not doing all of the collection types here, just a sampling.
        targetList = new ArrayList();
        targetList.add("abc");
        targetList.add("def");

        source = new String[] { "abc", "def" };

        // list from a string array
        converted = convert(source, new ReifiedType(List.class));
        AssertionService.assertEquals(this, "Failed array to collection conversion", targetList, converted);

        listSource = new LinkedList();
        listSource.add("abc");
        listSource.add("def");

        // list from another list source...we'll use a concrete list target this time to force a conversion
        converted = convert(listSource, new ReifiedType(ArrayList.class));
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetList, converted);

        // the target is a collecton SubType with a no-argument constructor. This should be
        // instantiated and all elements added to the SubType.  We'll test this with a custom
        // type that the implementation would not know about explicitly.
        converted = convert(listSource, new ReifiedType(CollectionSubTypeImpl.class));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof CollectionSubTypeImpl);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetList, converted);

        // array of primitives to a list...these will be wrappered
        targetList = new ArrayList();
        targetList.add(new Integer(1));
        targetList.add(new Integer(2));

        source = new int[] { 1, 2 };

        converted = convert(source, new ReifiedType(List.class));
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetList, converted);

        // the target is a collecton SubType with a no-argument constructor. This should be
        // instantiated and all elements added to the SubType.

        converted = convert(source, new ReifiedType(CollectionSubTypeImpl.class));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof CollectionSubTypeImpl);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetList, converted);

        // pseudo-generic conversion.  The elements must be converted to the target types.
        listSource = new LinkedList();
        listSource.add("1");
        listSource.add("2");

        converted = convert(listSource, new CheckerReifiedType(List.class, new Class[] {Integer.class} ));
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetList, converted);

        // Conversion step 5:  The target implements Map or Dictionary.  The source must be a map or dictionary
        // as well.

        Map targetMap = new HashMap();
        targetMap.put("abc", "123");
        targetMap.put("def", "456");

        Map sourceMap = new TreeMap(targetMap);
        // conversion between map types
        converted = convert(sourceMap, new ReifiedType(HashMap.class));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof HashMap);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetMap, converted);

        Properties targetProps = new Properties();
        targetProps.putAll(targetMap);

        // convert from a map to Properties
        converted = convert(sourceMap, new ReifiedType(Properties.class));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof Properties);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetProps, converted);

        // convert from a Dictionary to a Map
        converted = convert(targetProps, new ReifiedType(TreeMap.class));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof TreeMap);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetMap, converted);

        // the target is a Map SubType with a no-argument constructor. This should be
        // instantiated and all elements added to the SubType.  We'll test this with a custom
        // type that the implementation would not know about explicitly.
        converted = convert(sourceMap, new ReifiedType(MapSubTypeImpl.class));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof MapSubTypeImpl);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetMap, converted);

        converted = convert(sourceMap, new ReifiedType(DictionarySubTypeImpl.class));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof DictionarySubTypeImpl);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetProps, converted);

        // pseudo generic conversion of map types.  We'll convert both the keys and the values
        sourceMap = new HashMap();
        sourceMap.put("1", "java.lang.String");

        targetMap = new HashMap();
        targetMap.put(new Integer(1), String.class);

        converted = convert(sourceMap, new CheckerReifiedType(HashMap.class, new Class[] { Integer.class, Class.class }));
        AssertionService.assertTrue(this, "Incorrect map conversion class", converted instanceof HashMap);
        AssertionService.assertEquals(this, "Failed collection to collection conversion", targetMap, converted);

        // Conversion step 6:  Conversion from wrapper to a primitive type;

        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", new Integer(1), convert(new Integer(1), new ReifiedType(Integer.TYPE)));
        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", new Long(1), convert(new Long(1), new ReifiedType(Long.TYPE)));
        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", new Short((short)1), convert(new Short((short)1), new ReifiedType(Short.TYPE)));
        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", new Byte((byte)1), convert(new Byte((byte)1), new ReifiedType(Byte.TYPE)));
        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", new Character('A'), convert(new Character('A'), new ReifiedType(Character.TYPE)));
        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", Boolean.TRUE, convert(Boolean.TRUE, new ReifiedType(Boolean.TYPE)));
        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", new Double(1.0), convert(new Double(1.0), new ReifiedType(Double.TYPE)));
        AssertionService.assertEquals(this, "Incorrect wrapper/primitive conversion", new Float(1.0), convert(new Float(1.0), new ReifiedType(Float.TYPE)));

        // Conversion step 7:  Conversion between Number types we'll just do everything to Integer. Note that Boolean and Character are NOT Numbers

        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Long(1), new ReifiedType(Integer.class)));
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Short((short)1), new ReifiedType(Integer.class)));
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Byte((byte)1), new ReifiedType(Integer.class)));
        // Note:  the precision loss is allowed here
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Double(1.1), new ReifiedType(Integer.class)));
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Float(1.1), new ReifiedType(Integer.class)));

        // this is a combination of 6 and 7.  The primitive should be treated as the wrapper
        // equivalent, so this should convert
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Long(1), new ReifiedType(Integer.TYPE)));
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Short((short)1), new ReifiedType(Integer.TYPE)));
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Byte((byte)1), new ReifiedType(Integer.TYPE)));
        // Note:  the precision loss is allowed here
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Double(1.1), new ReifiedType(Integer.TYPE)));
        AssertionService.assertEquals(this, "Incorrect Number conversion", new Integer(1), convert(new Float(1.1), new ReifiedType(Integer.TYPE)));

        // Conversion step 8:  If the source is a String, there are additional defined conversions. This will not be exhaustive, but we'll
        // do one variation for each type.

        AssertionService.assertEquals(this, "Incorrect String conversion", Boolean.TRUE, convert("true", new ReifiedType(Boolean.TYPE)));
        AssertionService.assertEquals(this, "Incorrect String conversion", Boolean.TRUE, convert("true", new ReifiedType(Boolean.class)));

        AssertionService.assertEquals(this, "Incorrect String conversion", new Locale("en", "US"), convert("en_US", new ReifiedType(Locale.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", Pattern.compile("a*b").toString(), convert("a*b", new ReifiedType(Pattern.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", ConversionServiceChecker.class, convert("org.osgi.test.cases.blueprint.components.injection.ConversionServiceChecker", new ReifiedType(Class.class)));

        // the rest of the primitives are tested here.  The String/wrapper conversions are handled
        // by step 8.
        AssertionService.assertEquals(this, "Incorrect String conversion", new Integer(1), convert("1", new ReifiedType(Integer.TYPE)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Short((short)1), convert("1", new ReifiedType(Short.TYPE)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Long(1), convert("1", new ReifiedType(Long.TYPE)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Byte((byte)1), convert("1", new ReifiedType(Byte.TYPE)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Character('A'), convert("A", new ReifiedType(Character.TYPE)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Double(1.0), convert("1.0", new ReifiedType(Double.TYPE)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Float(1.0), convert("1.0", new ReifiedType(Float.TYPE)));

        // Conversion step 8:  If the target class has a constructor that takes a single String argument,
        // use that to create the class.  This includes the wrapper classes
        AssertionService.assertEquals(this, "Incorrect String conversion", new Integer(1), convert("1", new ReifiedType(Integer.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Short((short)1), convert("1", new ReifiedType(Short.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Long(1), convert("1", new ReifiedType(Long.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Byte((byte)1), convert("1", new ReifiedType(Byte.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Character('A'), convert("A", new ReifiedType(Character.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Double(1.0), convert("1.0", new ReifiedType(Double.class)));
        AssertionService.assertEquals(this, "Incorrect String conversion", new Float(1.0), convert("1.0", new ReifiedType(Float.class)));

        // this is a custom class that fits the pattern of having an appropriate constructor (and no type converter is installed)
        AssertionService.assertEquals(this, "Incorrect String conversion", new EuropeanRegionCode("UK+32"), convert("UK+32", new ReifiedType(EuropeanRegionCode.class)));

        // now a few scenarios where the spec specifically says things are not-convertible

        // Conversion step 3:  The conversion fails if the target is an array and the source is not an array or Collection.
        convertFailure("ABC", new ReifiedType(Object[].class));
        // Conversion step 3:  The source elements cannot be type converted into the target type
        listSource = new ArrayList();
        listSource.add("abc");
        convertFailure(listSource, new ReifiedType(int[].class));
        convertFailure(new Object[] { "abc" }, new ReifiedType(int[].class));

        // Conversion step 4:  The conversion fails if the target is a Collection and the source is not an array or Collection
        convertFailure("ABC", new ReifiedType(List.class));

        // Conversion step 4:  The target is an interface that extends Collection, but there
        // is no defined concrete type
        convertFailure(new Object[] { "abc" }, new ReifiedType(CollectionSubType.class));

        // Conversion step 4:  The target implements Collection, but does not have a no-arg constructor
        convertFailure(new String[] { "abc" }, new ReifiedType(BadCollectionSubTypeImpl.class));

        // Conversion step 5:  The conversion fails if the target is not a Map or Dictionary
        convertFailure("ABC", new ReifiedType(Map.class));

        // Conversion step 5:  The target is an interface that extends Map, but there
        // is no defined concrete type
        convertFailure(new Object[] { "abc" }, new ReifiedType(MapSubType.class));

        // Conversion step 5:  The target implements Map, but does not have a no-arg constructor
        convertFailure(sourceMap, new ReifiedType(BadMapSubTypeImpl.class));

        // Conversion step 5:  The target implements Dictionary, but does not have a no-arg constructor
        convertFailure(sourceMap, new ReifiedType(BadDictionarySubTypeImpl.class));

        // Conversion step 8:  No matches so far, and the source type is not a String.
        convertFailure(new Object(), new ReifiedType(String.class));

        // Conversion step 9:  The target type does not have a single String argument constructor
        convertFailure("ABC", new ReifiedType(BitSet.class));
    }


    /**
     * Do a protected version to a type.
     *
     * @param source The source object.
     * @param type   The target type.
     *
     * @return The converted object, or null if there was an exception.
     */
    protected Object convert(Object source, ReifiedType type) {
        try {
            // check that canConvert is also true.
            AssertionService.assertTrue(this, "Incorrect canConvert() result", blueprintConverter.canConvert(source, type));
            return blueprintConverter.convert(source, type);
        } catch (Exception e) {
            AssertionService.fail(this, "Unexpected exception converting object", e);
        }
        return null;
    }


    /**
     * Do a conversion operation that the spec identifies as a conversion failure scenario.
     *
     * @param source The source object.
     * @param type   The target type.
     */
    protected void convertFailure(Object source, ReifiedType type) {
        try {
            Object converted = blueprintConverter.convert(source, type);
            AssertionService.fail(this, "Expected conversion failure not received.");
        } catch (Exception e) {
            // we expect to get here...the conversion in questions should be an error
        }
    }


    class CheckerReifiedType extends ReifiedType {
        protected Class[] argumentTypes;

        public CheckerReifiedType(Class rawClass, Class[] argumentTypes) {
            super(rawClass);
            this.argumentTypes = argumentTypes;
        }

		/**
		 * Access to a type argument.
		 *
		 * The type argument refers to a argument in a generic type declaration
		 * given by index <code>i</code>. This method returns a Collapsed Type
		 * that has Object as class when no generic type information is available.
		 * Any object is assignable to Object and therefore no conversion is then
		 * necessary, this is compatible with older Javas.
		 *
		 * This method should be overridden by a subclass that provides access to
		 * the generic information.
		 *
		 * For example, in the following example:
		 *
		 * <pre>
		 * Map&lt;String, Object&gt; map;
		 * </pre>
		 *
		 * The type argument 0 is <code>String</code>, and type argument 1 is
		 * <code>Object</code>.
		 *
		 * @param i
		 *            The index of the type argument
		 * @return A Collapsed Type that represents a type argument. If
		 */
        public ReifiedType getActualTypeArgument(int i) {
            return new ReifiedType(argumentTypes[i]);
        }
		/**
		 * Return the number of type arguments.
		 *
		 * This method should be overridden by a subclass for Java 5 types.
		 *
		 * @return 0, subclasses must override this
		 */
        public int size() {
            return argumentTypes.length;
        }
    }
}


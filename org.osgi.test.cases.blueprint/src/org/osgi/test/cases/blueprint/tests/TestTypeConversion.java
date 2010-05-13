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

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Dictionary;
import java.util.Properties;

import org.osgi.service.blueprint.reflect.Target;
import org.osgi.test.cases.blueprint.components.injection.AsianRegionCode;
import org.osgi.test.cases.blueprint.components.injection.EuropeanRegionCode;
import org.osgi.test.cases.blueprint.components.injection.RegionCode;
import org.osgi.test.cases.blueprint.components.injection.CollectionSubTypeImpl;
import org.osgi.test.cases.blueprint.components.injection.DictionarySubTypeImpl;
import org.osgi.test.cases.blueprint.components.injection.MapSubTypeImpl;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.StandardErrorTestController;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests specificcally for testing defined builtin type conversion.
 *
 * @version $Id$
 */
public class TestTypeConversion extends DefaultTestBundleControl {
    /*
     * Tests the blueprint converter service with variations non-generic based conversions.
     */
    public void testBlueprintConverter() throws Exception {
        // this should just be the standard test set
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/blueprintConverter_checker.jar");

        // validation is all done in the component code
        controller.run();
    }


    /*
     * Tests the same type conversions preformed from the config files (at least the non-error conversions)
     */
    public void testBuiltinTypeConversions() throws Exception {
        // this should just be the standard error set
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/builtin_type_conversions.jar");

        MetadataEventSet startEvents = controller.getStartEvents();

        startEvents.validateComponentProperty("assignableRegionCode", "regionCode", new EuropeanRegionCode("UK+24"), RegionCode.class);
        startEvents.validateComponentProperty("wrapperToPrim", "primInteger", new Integer(1), Integer.TYPE);
        startEvents.validateComponentProperty("primToWrapper", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("convertedRegionCode", "regionCode", new AsianRegionCode("CN+90"), RegionCode.class);
        startEvents.validateComponentProperty("convertedAsianRegionCode", "asianRegionCode", new AsianRegionCode("CN+94"), AsianRegionCode.class);
        startEvents.validateComponentProperty("stringArrayToIntArray", "primIntArray", new int[] {1, 2}, int[].class);
        startEvents.validateComponentProperty("integerArrayToIntArray", "primIntArray", new int[] {1, 2}, int[].class);
        startEvents.validateComponentProperty("intArrayToIntegerArray", "wrappedIntArray", new Integer[] {new Integer(1), new Integer(2)}, Integer[].class);
        startEvents.validateComponentProperty("stringArrayToRegionCodeArray", "regionCodeArray", new RegionCode[] {new AsianRegionCode("CN+76") }, RegionCode[].class);
        startEvents.validateComponentProperty("stringListToIntArray", "primIntArray", new int[] {1, 2}, int[].class);
        startEvents.validateComponentProperty("integerListToIntArray", "primIntArray", new int[] {1, 2}, int[].class);
        startEvents.validateComponentProperty("stringListToRegionCodeArray", "regionCodeArray", new RegionCode[] {new AsianRegionCode("CN+76") }, RegionCode[].class);

        List target = new ArrayList();
        target.add("1");
        target.add("2");
        startEvents.validateComponentProperty("stringArrayToList", "list", target, List.class);
        startEvents.validateComponentProperty("stringListToLinkedList", "linkedList", target, LinkedList.class);
        startEvents.validateComponentProperty("stringArrayToCollectionSubTypeImpl", "collectionSubTypeImpl", target, CollectionSubTypeImpl.class);

        target = new ArrayList();
        target.add(new Integer(1));
        target.add(new Integer(2));
        startEvents.validateComponentProperty("intArrayToList", "list", target, List.class);

        Map mapTarget = new HashMap();
        mapTarget.put("abc", "123");
        mapTarget.put("def", "456");

        Properties propTarget = new Properties();
        propTarget.put("abc", "123");
        propTarget.put("def", "456");

        startEvents.validateComponentProperty("mapToTreeMap", "treeMap", mapTarget, TreeMap.class);
        startEvents.validateComponentProperty("mapToProperties", "properties", propTarget, Properties.class);
        startEvents.validateComponentProperty("mapToMapSubType", "mapSubTypeImpl", mapTarget, MapSubTypeImpl.class);
        startEvents.validateComponentProperty("mapToDictionarySubType", "dictionarySubTypeImpl", propTarget, DictionarySubTypeImpl.class);

        // wrapper to corresponding primitive

        startEvents.validateComponentProperty("wrapperToInt", "primInteger", new Integer(1), Integer.TYPE);
        startEvents.validateComponentProperty("wrapperToLong", "primLong", new Long(1), Long.TYPE);
        startEvents.validateComponentProperty("wrapperToShort", "primShort", new Short((short)1), Short.TYPE);
        startEvents.validateComponentProperty("wrapperToByte", "primByte", new Byte((byte)1), Byte.TYPE);
        startEvents.validateComponentProperty("wrapperToChar", "primCharacter", new Character('A'), Character.TYPE);
        startEvents.validateComponentProperty("wrapperToBoolean", "primBoolean", Boolean.TRUE, Boolean.TYPE);
        startEvents.validateComponentProperty("wrapperToDouble", "primDouble", new Double(1), Double.TYPE);
        startEvents.validateComponentProperty("wrapperToFloat", "primFloat", new Float(1), Float.TYPE);

        // Number-type conversions
        startEvents.validateComponentProperty("longToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("shortToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("byteToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("doubleToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("floatToInteger", "integer", new Integer(1), Integer.class);

        // primitive number-type conversions
        startEvents.validateComponentProperty("primLongToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("primShortToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("primByteToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("primDoubleToInteger", "integer", new Integer(1), Integer.class);
        startEvents.validateComponentProperty("primFloatToInteger", "integer", new Integer(1), Integer.class);

        controller.run();
    }

    /*
     * Test an array target with a non-array or collection source.
     */
    public void testArrayTargetBadSource() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_array_target_bad_source.jar");

        // validation is all done in the component code
        controller.run();
    }


    /*
     * Test an array target with a bad element conversion
     */
    public void testArrayTargetBadElement() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_array_target_bad_element.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test a collection target with a non-array or collection source
     */
    public void testCollectionTargetBadSource() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_collection_target_bad_source.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test a collection target with a custom interface only target type
     */
    public void testCollectionTargetInterfaceOnly() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_collection_target_interface_only.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test a collection target with a custom target type without a no-arg constructor
     */
    public void testCollectionTargetBadSubType() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_collection_target_bad_subtype.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test a map target with a non-map, non-dictionary source
     */
    public void testMapTargetBadSource() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_map_target_bad_source.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test a map target with an interface only target type
     */
    public void testMapTargetInterfaceOnly() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_map_target_interface_only.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test a map target with a target type without a no-arg constructor
     */
    public void testMapTargetBadSubType() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_map_target_bad_subtype.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test a dictionary target with a target type without a no-arg constructor
     */
    public void testDictionaryTargetBadSubType() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_dictionary_target_bad_subtype.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test non-String source object to terminate the conversion searches
     */
    public void testNonStringSource() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_non_string_source.jar");

        // validation is all done in the component code
        controller.run();
    }

    /*
     * Test String, but no conversion strategy for the target
     */
    public void testStringSourceNoConstructor() throws Exception {
        // this should just be the standard test set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/error_string_source_no_constructor.jar");

        // validation is all done in the component code
        controller.run();
    }
}

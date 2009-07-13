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

package org.osgi.test.cases.blueprint.java5.tests;

import java.net.URL;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Properties;
import java.util.Set;

import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.java5.components.injection.GenericListInjection;
import org.osgi.test.cases.blueprint.java5.components.injection.Point;
import org.osgi.test.cases.blueprint.java5.components.injection.Suit;
import org.osgi.test.cases.blueprint.java5.components.injection.GenericHolder;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains primitive string to type injection convertions
 *
 * @version $Revision$
 */
public class TestGenericCollectionInjection extends DefaultTestBundleControl {
    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value, Class type) {
        startEvents.validateComponentArgument(id, "arg1", value, type);
    }


    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Object propertyValue, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
    }


    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Object propertyValue, Class type, Class implementation) {
        startEvents.addValidator(new PropertyValueValidator(compName, propertyName, propertyValue, type, implementation));
    }

    public void testGenericCollectionInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/generic_collection_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // There's nothing special about the metadata here, so we're really only interested in the fact
        // the collections get converted appropriately.

        // array of ints converted into a Generic list of Point items
        {
            List<Integer> expected = new ArrayList<Integer>();
            expected.add(new Integer(123));
            expected.add(new Integer(456));
            startEvents.validateComponentArgument("GenericListConstructor", "arg1", expected, List.class);
        }

        {
            List<Class> expected = new ArrayList<Class>();
            expected.add(String.class);
            expected.add(GenericListInjection.class);
            startEvents.validateComponentProperty("GenericListProperty", "classList", expected, List.class);
        }

        {
            List<Integer> expected = new ArrayList<Integer>();
            expected.add(new Integer(123));
            expected.add(new Integer(456));
            startEvents.validateComponentProperty("GenericListExtendsProperty", "extendsList", expected, List.class);
        }

        {
            List<Integer> expected = new ArrayList<Integer>();
            expected.add(new Integer(123));
            expected.add(new Integer(456));
            startEvents.validateComponentProperty("GenericListSuperProperty", "superList", expected, List.class);
        }

        {
            List<Double> expected = new ArrayList<Double>();
            expected.add(new Double(123));
            expected.add(new Double(456));
            startEvents.validateComponentProperty("GenericListAProperty", "a", expected, List.class);
        }

        {
            Set<Double> expected = new HashSet<Double>();
            expected.add(new Double(3.1415926));
            expected.add(new Double(1.0));
            startEvents.validateComponentArgument("GenericSetConstructor", "arg1", expected, Set.class);
        }

        {
            Set<Integer> expected = new HashSet<Integer>();
            expected.add(new Integer(123));
            expected.add(new Integer(456));
            startEvents.validateComponentProperty("GenericSetProperty", "integer", expected, Set.class);
        }

        {
            Set<URL> expected = new HashSet<URL>();
            expected.add(new URL("http://www.osgi.org"));
            expected.add(new URL("http://www.w3c.org"));
            startEvents.validateComponentProperty("GenericSetProperty", "url", expected, Set.class);
        }

        {
            Map<Long, Boolean> expected = new HashMap<Long, Boolean>();
            expected.put(new Long(1), Boolean.TRUE);
            expected.put(new Long(2), Boolean.FALSE);
            expected.put(new Long(3), Boolean.TRUE);
            expected.put(new Long(4), Boolean.FALSE);
            startEvents.validateComponentArgument("GenericMapConstructor", "arg1", expected, Map.class);
        }

        {
            Map<Double, String> expected = new HashMap<Double, String>();
            expected.put(new Double(1.0), "true");
            expected.put(new Double(2.0), "false");
            startEvents.validateComponentProperty("GenericMapProperty", "map1", expected, Map.class);
        }

        {
            Map<String, URL> expected = new HashMap<String, URL>();
            expected.put("osgi", new URL("http://www.osgi.org"));
            expected.put("w3c", new URL("http://www.w3c.org"));
            startEvents.validateComponentProperty("GenericMapProperty", "map2", expected, Map.class);
        }

        {
            Map expected = new HashMap();
            expected.put("abc", "1");
            expected.put("def", "2");
            addPropertyValidator(startEvents, "mapToConcurrentMap", "concurrentMap", expected, ConcurrentMap.class, ConcurrentHashMap.class);
        }

        {
            // <list> will remain the same, and <array> will be converted into an array list
            Collection expectedList = new ArrayList();
            expectedList.add("abc");
            expectedList.add("def");
            expectedList.add("def");

            // this is the result expected when the source is a set...the dup has been removed
            Collection expectedSetList = new ArrayList();
            expectedList.add("abc");
            expectedList.add("def");

            // these should all end up being the same type
            addPropertyValidator(startEvents, "setToQueue", "queue", expectedSetList, Queue.class, LinkedList.class);
            addPropertyValidator(startEvents, "listToQueue", "queue", expectedList, Queue.class, LinkedList.class);
            addPropertyValidator(startEvents, "arrayToQueue", "queue", expectedList, Queue.class, LinkedList.class);
        }

        controller.run();
    }


    /**
     * Some very specific generic tests.
     *
     * @exception Exception
     */
    public void testGenericPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/generic_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // There's nothing special about the metadata here, so we're really only interested in the fact
        // the collections get converted appropriately.

        // simple list of strings
        {
            List<Point> expected = new LinkedList<Point>();
            expected.add(new Point(123, 456));
            expected.add(new Point(0, 0));
            startEvents.validateComponentProperty("GenericListConversion", "pointList", expected, LinkedList.class);
        }

        {
            Map<String, Point> expected = new TreeMap<String, Point>();
            expected.put("Start", new Point(123, 456));
            expected.put("Finish", new Point(0, 0));
            startEvents.validateComponentProperty("GenericMapConversion", "pointMap", expected, TreeMap.class);
        }

        {
            GenericHolder<Boolean> expected = new GenericHolder<Boolean>(Boolean.TRUE);
            startEvents.validateComponentProperty("GenericConversion", "booleanHolder", expected, GenericHolder.class);
        }

        {
            startEvents.validateComponentArgument("EnumConversion", "suit", Suit.CLUBS, Suit.class);
        }

        controller.run();
    }


    /**
     * Import a list of ServiceReferences and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
     */
    public void testListCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/reference_list_import.jar",
                getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.BEAN_INIT_METHOD);
        controller.run();
    }


    /**
     * Import a list of services and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
     */
    public void testListCollectionServiceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/reference_list_service_import.jar",
                getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.BEAN_INIT_METHOD);
        controller.run();
    }


    /**
     * Import a collection of ServiceReferences and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
     */
    public void testCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/reference_collection_import.jar",
                getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.BEAN_INIT_METHOD);
        controller.run();
    }


    /**
     * Test conversion of string values to Pattern instances.
     */
    public void testPatternInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/pattern_injection.jar");

        // this one is self checking and will raise an assertion failure if something is wrong
        controller.run();
    }


    /**
     * tests conversion errors resulting from generic list injection.
     */
    public void testGenericConversionError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_generic_conversion.jar");
        controller.run();
    }


    /**
     * tests conversion errors resulting from inserting a generic raw class instance
     * into a qualified method with no available type converter.
     */
    public void testGenericRawClassError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_generic_rawclass.jar");
        controller.run();
    }


    /**
     * tests generic injection of a reference-list when the member type is incorrect.
     * The target property  is List<service> but the member type is service reference.
     */
    public void testReferenceListServiceError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_reference_list_service_import.jar");
        controller.run();
    }


    /**
     * tests generic injection of a reference-list when the member type is incorrect.
     * The target property  is List<ServiceReference> but the member type is service instance.
     */
    public void testReferenceListError() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_refereice_list_import.jar");
        controller.run();
    }
}


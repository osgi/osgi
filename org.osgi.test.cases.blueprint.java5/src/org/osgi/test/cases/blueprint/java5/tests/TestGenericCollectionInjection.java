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
package org.osgi.test.cases.blueprint.java5.tests;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.java5.components.injection.GenericListInjection;
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

	public void testGenericCollectionInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/generic_collection_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // There's nothing special about the metadata here, so we're really only interested in the fact
        // the collections get converted appropriately.

        // simple list of strings
        {
            List<Integer> expected = new ArrayList<Integer>();
            expected.add(new Integer(123));
            expected.add(new Integer(456));
            startEvents.validateComponentArgument("GenericListConstructor", "arg1", expected, List.class);
        }

        {
            List<String> expected = new ArrayList<String>();
            expected.add("abc");
            expected.add("def");
            startEvents.validateComponentProperty("GenericListProperty", "string", expected, List.class);
        }

        {
            List<Class> expected = new ArrayList<Class>();
            expected.add(String.class);
            expected.add(GenericListInjection.class);
            startEvents.validateComponentProperty("GenericListProperty", "classList", expected, List.class);
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
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }


	/**
	 * Import a set of ServiceReferences and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
	 */
	public void testSetCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/reference_set_import.jar",
            getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

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
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

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
}


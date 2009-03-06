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
package org.osgi.test.cases.blueprint.tests;

import java.text.SimpleDateFormat;

import org.osgi.test.cases.blueprint.components.comp1.SimpleTestComponent;
import org.osgi.test.cases.blueprint.components.factory.SimpleInstanceFactory;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests related with the framework class loading policies.
 *
 * @author left
 * @version $Revision$
 */
public class TestNamespaceHandler extends DefaultTestBundleControl {
	/*
	 * Tests a simple managed bundle with a single component, no explicit header specified.
	 */
	public void testDateHandlerExample() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/date_namespace_checker.jar");

        // this installs and starts the bundle containing the namespace handler before
        // starting the test
        controller.addSetupBundle(getWebServer()+"www/date_namespace_handler.jar");

        // we're mostly interested not receiving any failure-related events, but
        // we'll check some of the metadata to make sure that a)  Everything ended up
        // in there, and b)  That the metadata APIs still work through our injected
        // helper classes
        MetadataEventSet startEvents = controller.getStartEvents();
        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("format1", SimpleDateFormat.class,
            new TestParameter[] { new TestParameter("yyyy.MM.dd G 'at' HH:mm:ss z", 0) }, null)));

        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("format2", SimpleDateFormat.class,
            new TestParameter[] { new TestParameter("yyyy-MM-dd HH:mm", 0) },
            new TestProperty[] { new TestProperty("true", "lenient")})));

        // and this tests the inner component metadata
        startEvents.addValidator(new PropertyMetadataValidator("checker3",
            new TestProperty(new TestComponentValue(
            new LocalComponent(SimpleDateFormat.class,
                new TestParameter[] { new TestParameter("yyyy-MM-dd HH:mm", 0) },
                new TestProperty[] { new TestProperty("false", "lenient")})),
            "format")));

        controller.run();
    }
}

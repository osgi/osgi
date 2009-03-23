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

import java.util.Hashtable;

import org.osgi.framework.Bundle;

import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;
import org.osgi.test.cases.blueprint.components.serviceimport.ServiceTwoListener;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.*;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests actions performed on starting and stopping of the
 * extender bundle.
 *
 * @version $Revision$
 */
public class TestExtenderLifeCycle extends DefaultTestBundleControl {
    // the retrieved extender bundle
    protected Bundle extender = null;


    /**
     * Tests extender bundle startup.  This should locate
     * and start all installed blueprint bundles.
     *
     * @exception Exception
     */
	public void testExtenderStart() throws Exception {
        // go locate the current extender bundle
        Bundle extenderBundle = getExtenderBundle();
        // we want this in a stopped state initially.
        extenderBundle.stop();

        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/comp1_no_header.jar");
        // give this a little larger time out
        controller.setTimeout(30000);

        // now add some more selected tests to be sitting idle waiting for
        controller.addBundle(getWebServer()+"www/ServiceOne_import.jar");
        controller.addBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.addBundle(getWebServer()+"www/comp1_explicit_config.jar");

        // get the start events for the last installed bundle...that's the last one
        // to get its startup stuff processed
        MetadataEventSet startEvents = controller.getStartEvents(3);

        // have this start with a little delay behind the others
        startEvents.addInitializer(new TestBundleStarter(extenderBundle, 1000));
        // this should run through all of the normal startup/shutdown events from here.
        controller.run();
    }


    /**
     * Retrieve the extender bundle for a test.
     *
     * @return The extender Bundle instance.
     * @exception Exception
     */
    protected Bundle getExtenderBundle() throws Exception {
        if (extender == null) {
            // load a simple test bundle and run the test.  The extender bundle will
            // be obtained in the context of running this
            StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_no_header.jar");
            controller.run();
            extender = BaseTestController.getExtenderBundle();
        }
        return extender;
    }
}

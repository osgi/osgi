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

package org.osgi.test.cases.blueprint.framework;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Controller for a standard error test.  This is a single-phase test
 * that will validate that the ModuleContext is not created.
 */
public class StandardErrorTestController extends BaseTestController {
    // our stanard tests have just a single bundle use for
    // for the testing.
    protected Bundle bundle;
    // this is our startup phase.  General test progression
    // is start the bundle, wait for all of our expected
    // events.
    protected TestPhase testPhase;

    public StandardErrorTestController(BundleContext testContext, String testBundle) throws Exception {
        this(testContext, testBundle, DEFAULT_TIMEOUT);
    }

    public StandardErrorTestController(BundleContext testContext, String testBundle, long timeout) throws Exception {
        super(testContext, timeout);
        // we're responsible for the bundle install/uninstall.
        bundle = installBundle(testBundle);

        testPhase = new TestPhase(testContext, timeout);
        // A standard start/stop cycle test has a common set of events we look for
        // in each phase.
        testPhase.addEventSet(new StandardErrorEventSet(testContext, bundle));

        addTestPhase(testPhase);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test phase processor.
     */
    public TestPhase getTestPhase() {
        return testPhase;
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test event processor.
     */
    public EventSet getTestEvents() {
        return testPhase.getEventSet(0);
    }


    /**
     * Perform any controller-related testcase cleanup steps.
     *
     * @param runner The test case running controlling the tests.
     */
    public void cleanup() throws Exception {
        super.cleanup();
        // uninstall the bundle on completion
        bundle.uninstall();
    }
}


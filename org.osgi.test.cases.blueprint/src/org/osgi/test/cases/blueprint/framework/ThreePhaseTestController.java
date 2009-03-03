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
 * Controller for a standard, single bundle test with involving
 * a start/stop cycle on a bundle.
 */
public class ThreePhaseTestController extends StandardTestController {
    // this is our middle test phase.
    protected TestPhase middlePhase;

    public ThreePhaseTestController(BundleContext testContext) {
        super(testContext);
        middlePhase = new TestPhase(testContext, timeout);
        // this inserts this between the start and stop hases
        addTestPhase(1, middlePhase);
    }

    public ThreePhaseTestController(BundleContext testContext, String bundle1) throws Exception {
        this(testContext);
        addBundle(bundle1);
    }

    public ThreePhaseTestController(BundleContext testContext, String bundle1, String bundle2) throws Exception {
        this(testContext);
        addBundle(bundle1);
        addBundle(bundle2);
    }


    /**
     * Add a bundle/metadata combo to the target test controller.
     *
     * @param bundle The installed bundle.
     * @param moduleMetadata
     *               The associated module metadata.
     */
    public void addBundle(Bundle bundle, ModuleMetadata moduleMetadata) {
        // add the standard events, then our additions
        super.addBundle(bundle, moduleMetadata);

        // The middle event set has no standard set of items to add.  This is filled
        // in by the test creater.
        EventSet middleEvents = new MetadataEventSet(moduleMetadata, testContext, bundle);
        addMiddleEvents(bundle, moduleMetadata, middleEvents);
        middlePhase.addEventSet(middleEvents);
    }

    /**
     * Add a standard set of bundle middle events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param moduleMetadata
     *               The ModuleMetadata context for this event set.
     * @param events The created event set.
     */
    public void addMiddleEvents(Bundle bundle, ModuleMetadata moduleMetadata, EventSet events) {
        // no standard set for these
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The middle test phase processor.
     */
    public TestPhase getMiddlePhase() {
        return middlePhase;
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The middle phase test event processor.
     */
    public MetadataEventSet getMiddleEvents() {
        return getMiddleEvents(0);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The middle phase test event processor.
     */
    public MetadataEventSet getMiddleEvents(int index) {
        return (MetadataEventSet)middlePhase.getEventSet(0);
    }
}


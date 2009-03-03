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
 * An event set for processing all standard events we expect to
 * see from an "error" test case.  An error test is one that
 * starts a bundle and processes the error events.  No Module context
 * artifacts should exist at the end of this test.
 */
public class StandardErrorEventSet extends EventSet {
    public StandardErrorEventSet(BundleContext testBundle, Bundle bundle) {
        super(testBundle, bundle);
        // we add an initializer to start our bundle when the test starts
        addInitializer(new TestBundleStarter(bundle));
        // we always expect to see a started bundle event, even though
        // the managed bundle portion of this will give an error
        addBundleEvent("STARTED");
        // no ModuleContext service should be published for this bundle.  This is an error if
        // one shows up
        addFailureEvent(new ServiceTestEvent("REGISTERED", "org.osgi.service.blueprint.context.ModuleContext"));
        // we should be seeing a failure event published for this.
        // other events are not really determined.
        addBlueprintEvent("FAILURE");
        addModuleContextEvent("FAILED");

        // at the end of everything, there should not be a method context associated with this bundle
        addValidator(new NoModuleContextValidator());
        // the bundle should be in the ACTIVE state when everything settles down
        addValidator(new BundleStateValidator(Bundle.ACTIVE));
    }
}


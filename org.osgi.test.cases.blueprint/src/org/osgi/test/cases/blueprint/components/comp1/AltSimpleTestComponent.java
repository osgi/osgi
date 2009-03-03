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

package org.osgi.test.cases.blueprint.components.comp1;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * An alternate simple component used for testing init/destroy lifecycle
 * overrides.
 */
public class AltSimpleTestComponent extends BaseTestComponent {

    public AltSimpleTestComponent() {
        super("altcomp1");
    }

    public AltSimpleTestComponent(String componentId) {
        super(componentId);
    }

    /**
     * Standard init method...used to broadcast the init event.
     */
    public void init() {
        AssertionService.fail(this, "Incorrect init method called");
    }


    /**
     * Standard init method...used to broadcast the destroy event.
     */
    public void destroy() {
        AssertionService.fail(this, "Incorrect destroy method called");
    }

    /**
     * Standard init method...used to broadcast the init event.
     */
    public void altinit() {
        props.put(INIT_CALLED, Boolean.TRUE);
        AssertionService.sendEvent(this, AssertionService.COMPONENT_INIT_METHOD);
    }


    /**
     * Standard init method...used to broadcast the destroy event.
     */
    public void altdestroy() {
        props.put(DESTROY_CALLED, Boolean.TRUE);
        AssertionService.sendEvent(this, AssertionService.COMPONENT_DESTROY_METHOD);
    }
}


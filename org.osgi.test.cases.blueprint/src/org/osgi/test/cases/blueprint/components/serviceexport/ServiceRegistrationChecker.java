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

package org.osgi.test.cases.blueprint.components.serviceexport;

import java.util.Map;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Concrete target for testing import of ServiceRegistrations.  This will update
 * the service properties injected ServiceRegistration, which should trigger a
 * MODIFIED service event and be visible to the test controller for verification.
 */
public class ServiceRegistrationChecker extends BaseTestComponent {
    // our injected set of properties to update
    protected Hashtable setProperties;

    public ServiceRegistrationChecker(String componentId, Map props) {
        super(componentId);

        // we need a dictionary to update the service properties.
        setProperties = new Hashtable();
        // copy these entries
        setProperties.putAll(props);
    }

    public void setRegistration(ServiceRegistration reg) {
        AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
        reg.setProperties(setProperties);
    }
}


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

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestGoodService;


/**
 * Concrete target for a service reference.  This is a subclass of our base
 * test service to have a deeper hierarchy for service registrations.  This implements
 * all of the interface methods plus an additional concrete method that can be accessed.
 * This is a subclass of the base test component so we can track creation events when
 * lazy initialization is used.
 */
public class TestServiceFactory extends BaseTestComponent implements ServiceFactory {
    // the instance qualifier we add to each instantiated child service
    protected int instanceCount;

    public TestServiceFactory(String componentId) {
        super(componentId);
    }

    public Object getService(Bundle bundle, ServiceRegistration registration) {
        String newId = componentId + "_" + instanceCount++;

        Hashtable props = new Hashtable();
        props.put("instance.id", newId);
        // send a GET event for this.
        AssertionService.sendEvent(this, AssertionService.SERVICE_FACTORY_GET, props);
        // create an instance using a counter so we keep track of this
        return new TestGoodService(newId);
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
        Hashtable props = new Hashtable();
        props.put("instance.id", ((BaseTestComponent)service).getComponentId());
        // send a UNGET event for this.
        AssertionService.sendEvent(this, AssertionService.SERVICE_FACTORY_UNGET, props);
    }
}


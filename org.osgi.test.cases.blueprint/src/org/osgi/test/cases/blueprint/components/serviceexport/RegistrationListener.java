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
import java.util.Map;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class RegistrationListener extends BaseTestComponent {

    public RegistrationListener(String componentId) {
        super(componentId);
    }

    protected void registered(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        AssertionService.sendEvent(this, AssertionService.SERVICE_REGISTERED, props);
    }

    protected void unregistered(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        AssertionService.sendEvent(this, "UNSERVICE_REGISTERED", props);
    }
}


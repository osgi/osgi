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

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.StringValueDescriptor;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

public class ReferenceComponentValidator extends MetadataValidator {
    // the target componentId.
    protected String componentId;
    // the service instance interfaces
    protected Class[] interfaces;

    public ReferenceComponentValidator(String componentId, Class iface) {
        this(componentId, new Class[] { iface });
    }

    public ReferenceComponentValidator(String componentId, Class[] interfaces) {
        this.componentId = componentId;
        this.interfaces = interfaces;
    }

    public void validate(BundleContext testContext) throws Exception {
        // ensure we have everything initialized
        super.validate(testContext);
        // the value must be equal

        Object componentObject = moduleMetadata.getComponent(componentId);
        assertNotNull("Null reference component found for " + componentId, componentObject);
        for (int i = 0; i < interfaces.length; i++) {
            assertTrue("Service reference object for " + componentId + " does not implement " + interfaces[i].getName(),
                interfaces[i].isInstance(componentObject));
        }
    }
}



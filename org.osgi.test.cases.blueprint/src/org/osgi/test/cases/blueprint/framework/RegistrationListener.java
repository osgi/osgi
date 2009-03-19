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
import junit.framework.Assert;

import org.osgi.service.blueprint.reflect.ComponentValue;
import org.osgi.service.blueprint.reflect.ReferenceValue;
import org.osgi.service.blueprint.reflect.RegistrationListenerMetadata;
import org.osgi.service.blueprint.reflect.Value;

/**
 * Validate the metadata for a registration listener.
 */
public class RegistrationListener extends Assert {
    // the listener component name (can be null if we expect an anonymous component)
    protected String componentId;
    // the registration method name.
    protected String registrationName;
    // the unregistration method name
    protected String unregistrationName;

    /**
     * Create a registraiton listener definition.
     *
     * @param componentId
     *               The id of the target component (can be null if this service
     *               uses an inner component).
     * @param registratonName
     *               The name of the registration method.
     * @param unregistrationName
     *               The name of the unregistration method.
     */
    public RegistrationListener(String componentId, String registrationName, String unregistrationName) {
        this.componentId = componentId;
        this.registrationName = registrationName;
        this.unregistrationName = unregistrationName;
    }

    /**
     * Determine if this descriptor matches the basic attributes of
     * an exported service.  This is used to locate potential matches.
     *
     * @param meta   The candidate exported service
     *
     * @return true if this service matches on all of the specifics.
     */
    public boolean matches(RegistrationListenerMetadata meta) {

        Value component = meta.getListenerComponent();
        // if we have an explicit component id, we need to verify this
        if (componentId != null) {
            // this must be a reference to a component
            if (!(component instanceof ReferenceValue)) {
                return false;
            }
            // the component names must match
            if (!componentId.equals(((ReferenceValue)component).getComponentName())) {
                return false;
            }
        }
        else {
            // this must be an inner component
            // this must be a reference to a component
            if (!(component instanceof ComponentValue)) {
                return false;
            }
        }

        if (registrationName != null && !registrationName.equals(meta.getRegistrationMethodName())) {
            return false;
        }

        if (unregistrationName != null && !unregistrationName.equals(meta.getUnregistrationMethodName())) {
            return false;
        }

        return true;
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(RegistrationListenerMetadata meta) throws Exception {
        // no addtional validation
    }
}


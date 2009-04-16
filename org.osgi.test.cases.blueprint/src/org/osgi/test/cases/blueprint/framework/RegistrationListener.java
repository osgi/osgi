/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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


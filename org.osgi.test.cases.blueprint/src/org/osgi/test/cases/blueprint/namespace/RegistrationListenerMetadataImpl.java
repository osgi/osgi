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

package org.osgi.test.cases.blueprint.namespace;

import org.osgi.service.blueprint.reflect.RegistrationListenerMetadata;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A ComponentMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class RegistrationListenerMetadataImpl implements RegistrationListenerMetadata {
    private Value listenerComponent;
    private String registrationMethodName;
    private String unregistrationMethodName;

    public RegistrationListenerMetadataImpl() {
    }

    public RegistrationListenerMetadataImpl(RegistrationListenerMetadata source) {
        listenerComponent = NamespaceUtil.cloneValue(source.getListenerComponent());
        registrationMethodName = source.getRegistrationMethodName();
        unregistrationMethodName = source.getUnregistrationMethodName();
    }



    /**
     * The component instance that will receive registration and unregistration
     * events. The returned value must reference a component and therefore be
     * either a ComponentValue, ReferenceValue, or ReferenceNameValue.
     *
     * @return the listener component reference.
     */
    public Value getListenerComponent() {
        return listenerComponent;
    }

    public void setListenerComponent(Value v) {
        listenerComponent = v;
    }


    /**
     * The name of the method to invoke on the listener component when
     * the exported service is registered with the service registry.
     *
     * @return the registration callback method name.
     */
    public String getRegistrationMethodName() {
        return registrationMethodName;
    }

    public void setRegistrationMethodName(String name) {
        registrationMethodName = name;
    }


    /**
     * The name of the method to invoke on the listener component when
     * the exported service is unregistered from the service registry.
     *
     * @return the unregistration callback method name.
     */
    public String getUnregistrationMethodName() {
        return unregistrationMethodName;
    }

    public void setUnregistrationMethodName(String name) {
        unregistrationMethodName = name;
    }
}


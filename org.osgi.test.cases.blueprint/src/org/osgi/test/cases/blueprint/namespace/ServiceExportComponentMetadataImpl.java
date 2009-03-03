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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ConstructorInjectionMetadata;
import org.osgi.service.blueprint.reflect.MethodInjectionMetadata;
import org.osgi.service.blueprint.reflect.PropertyInjectionMetadata;
import org.osgi.service.blueprint.reflect.RegistrationListenerMetadata;
import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;
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
public class ServiceExportComponentMetadataImpl extends ComponentMetadataImpl implements ServiceExportComponentMetadata {
    private Map serviceProperties;
    private Value exportedComponent;
    private int ranking = 0;
    private Set interfaceNames;
    private Collection registrationListeners;
    private int exportMode = EXPORT_MODE_DISABLED;


    public ServiceExportComponentMetadataImpl() {
        this((String)null);
    }

    public ServiceExportComponentMetadataImpl(String name) {
        super(name);
        // create a new one of these, even if empty
        serviceProperties = new HashMap();
        interfaceNames = new HashSet();
        registrationListeners = new ArrayList();
    }


    public ServiceExportComponentMetadataImpl(ServiceExportComponentMetadata source) {
        super(source);
        serviceProperties = new HashMap(source.getServiceProperties());
        exportedComponent = NamespaceUtil.cloneValue(exportedComponent);
        ranking = source.getRanking();
        interfaceNames = new HashSet(source.getInterfaceNames());
        Iterator i = source.getRegistrationListeners().iterator();
        registrationListeners = new ArrayList();
        while (i.hasNext()) {
            registrationListeners.add(new RegistrationListenerMetadataImpl((RegistrationListenerMetadata)i.next()));
        }
        exportMode = source.getAutoExportMode();
    }



    /**
     * The user declared properties to be advertised with the service.
     *
     * @return Map containing the set of user declared service properties (may be
     * empty if no properties were specified).
     */
    public Map getServiceProperties() {
        return serviceProperties;
    }

    public void setServiceProperties(Map p) {
        serviceProperties = p;
    }

    /**
     * The component that is to be exported as a service. Value must refer to a component and
     * therefore be either a ComponentValue or ReferenceValue.
     *
     * @return the component to be exported as a service.
     */
    public Value getExportedComponent() {
        return exportedComponent;
    }

    public void setExportedComponent(Value v) {
        exportedComponent = v;
    }

    /**
     * The ranking value to use when advertising the service
     *
     * @return service ranking
     */
    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    /**
     * The type names of the set of interface types that the service should be advertised
     * as supporting.
     *
     * @return an immutable set of (String) type names, or an empty set if using auto-export
     */
    public Set getInterfaceNames() {
        return interfaceNames;
    }

    public void addInterfaceName(String name) {
        interfaceNames.add(name);
    }


    /**
     * The listeners that have registered to be notified when the exported service
     * is registered and unregistered with the framework.
     *
     * @return an immutable collection of RegistrationListenerMetadata
     */
    public Collection getRegistrationListeners() {
        return registrationListeners;
    }

    public void addRegistrationListener(RegistrationListenerMetadata l) {
        registrationListeners.add(l);
    }

    /**
     * Return the auto-export mode specified.
     *
     * @return One of EXPORT_MODE_DISABLED, EXPORT_MODE_INTERFACES, EXPORT_MODE_CLASS_HIERARCHY, EXPORT_MODE_ALL
     */
    public int getAutoExportMode() {
        return exportMode;
    }


    public void setAutoExportMode(int mode) {
        exportMode = mode;
    }
}


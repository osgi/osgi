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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.service.blueprint.reflect.BindingListenerMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ComponentValue;
import org.osgi.service.blueprint.reflect.ReferenceValue;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.UnaryServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.Value;
import org.osgi.test.cases.blueprint.services.TestUtil;

import junit.framework.Assert;

/**
 * A single referenced service in the ModuleContext metadata.
 */
public class ReferencedService extends ReferencedServiceBase implements TestComponentMetadata {
    // the default timeout value
    public static final int DEFAULT_TIMEOUT = 300000;

    // the service timeout value
    protected long timeout;


    /**
     * Create a ReferenceService descriptor from a single interface.
     *
     * @param interfaceClass
     *                  A single interface class used for the reference.
     * @param availability
     *                  The availability setting.
     * @param filter    The declared filter string for the reference.
     * @param listeners An expected set of listener metadata.
     */
    public ReferencedService(String name, Class interfaceClass, int availability, String filter, BindingListener[] listeners, long timeout) {
        this(name, new Class[] { interfaceClass }, availability, filter, listeners, timeout);
    }

    /**
     * Create a ReferenceService descriptor.
     *
     * @param interfaces The set of interfaces to access.
     * @param availability
     *                   The availability setting.
     * @param filter     The declared filter string for the reference.
     * @param listeners  An expected set of listener metadata.
     */
    public ReferencedService(String name, Class[] interfaces, int availability, String filter, BindingListener[] listeners, long timeout) {
        super(name, interfaces, availability, filter, listeners);
        this.timeout = timeout;
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, ServiceReferenceComponentMetadata metadata) throws Exception {
        assertTrue("Mismatch on service reference type", metadata instanceof UnaryServiceReferenceComponentMetadata);
        // do the base validation
        super.validate(moduleMetadata, metadata);
        assertEquals(timeout, ((UnaryServiceReferenceComponentMetadata)metadata).getTimeout());
    }

    /**
     * Determine if this descriptor matches the basic attributes of
     * an exported service.  This is used to locate potential matches.
     *
     * @param meta   The candidate exported service
     *
     * @return true if this service matches on all of the specifics.
     */
    public boolean matches(ComponentMetadata componentMeta) {
        // we only handle service reference component references.
        if (!(componentMeta instanceof UnaryServiceReferenceComponentMetadata)) {
            return false;
        }

        // match on the remaining bits
        return super.matches(componentMeta);
    }
}


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

import org.osgi.service.blueprint.reflect.BindingListenerMetadata;
import org.osgi.service.blueprint.reflect.ComponentValue;
import org.osgi.service.blueprint.reflect.ReferenceValue;
import org.osgi.service.blueprint.reflect.Value;

import junit.framework.Assert;

/**
 * A base type for validating BindingListener metadata.
 */
public class BindingListener extends Assert {
    // the listener component name.  This is either a ReferenceValue or a ComponentValue
    protected TestValue listener;
    // The listener Bind method name.
    protected String bindName;
    // the listener unbind method name
    protected String unbindName;

    public BindingListener(String componentName, String bindName, String unbindName) {
        this(new TestReferenceValue(componentName), bindName, unbindName);
    }

    public BindingListener(TestValue listener, String bindName, String unbindName) {
        this.listener = listener;
        this.bindName = bindName;
        this.unbindName = unbindName;
    }

    /**
     * Test if a binding listener from a set of binding listners
     * matches this expected value.
     *
     * @param meta   The target metadata for the match.
     *
     * @return True if this appears to be the validation target, false otherwise.
     */
    public boolean matches(BindingListenerMetadata meta) {
        Value component = meta.getListenerComponent();

        // the value type does the matching
        return listener.equals(component);
    }


    /**
     * Validate the remainder of the metadata in a binding listener.
     *
     * @param meta   The imput metadata.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, BindingListenerMetadata meta) throws Exception {
        assertEquals(bindName, meta.getBindMethodName());
        assertEquals(unbindName, meta.getUnbindMethodName());
        listener.validate(moduleMetadata, meta.getListenerComponent());
    }
}


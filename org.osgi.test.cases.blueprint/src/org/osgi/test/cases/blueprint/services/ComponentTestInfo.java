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

package org.osgi.test.cases.blueprint.services;
import java.util.Dictionary;

public interface ComponentTestInfo {
    public static final String COMPONENT_ID = "component.id";
    public static final String INIT_CALLED = "component.init";
    public static final String DESTROY_CALLED = "component.destroy";
    public static final String COMPONENT_INSTANCE = "component.instance";
    public static final String COMPONENT_PROPERTIES = "component.properties";
    public static final String COMPONENT_ARGUMENTS = "component.arguments";

    /**
     * Return a component identifier string used for determining
     * which component might be in error.
     *
     * @return The String id of the component.
     */
    public String getComponentId();


    /**
     * Returns a set of properties related to this created component.
     *
     * @return A dictionary with properties related to this component.  All
     *         components should return a standard property set, but specific
     *         ones migh return additional information.
     */
    public Dictionary getComponentProperties();


    /**
     * Retrieve a property for a given name from a component.
     *
     * @param propName The property name.
     *
     * @return Any object associated with that property.
     */
    public Object getProperty(String propName);


    /**
     * Check the initialization state for a component.  This is generally
     * called by one component's init() method to verifify that a
     * component it references is in the correct state.
     *
     * @return true if the component has been completely initialized, false if
     *         the call to the init method is still pending.
     */
    public boolean isInitialized();


    /**
     * Check the destroy state of a component.  This is normally called
     * from the destroy() method of another object to verify that
     * component termination is proceeding in the correct order.
     *
     * @return true if the component has already been destroyed.  false if the
     *         method is still pending termination.  Note that false is the
     *         successful return value here.
     */
    public boolean isDestroyed();
}


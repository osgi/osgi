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


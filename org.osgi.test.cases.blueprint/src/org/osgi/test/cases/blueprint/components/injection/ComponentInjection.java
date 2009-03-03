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

package org.osgi.test.cases.blueprint.components.injection;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

public class ComponentInjection extends BaseTestComponent {
    // list of initialized dependent components
    protected static Map dependentComponents = new HashMap();


    /**
     * Simple injection with just a component id.  This is used for tests that
     * only test property injection.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ComponentInjection(String componentId) {
        super(componentId);
        setArgumentValue("arg1", componentId);
    }

    /**
     * Simple injection with one component arguments.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ComponentInjection(String componentId, ComponentTestInfo arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
        // this should be fully initialized now
        validateComponentReference(arg2, true);
    }

    /**
     * Simple injection with two component arguments.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ComponentInjection(String componentId, ComponentTestInfo arg2, ComponentTestInfo arg3) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
        setArgumentValue("arg3", arg3);
        // this should be fully initialized now
        validateComponentReference(arg2, true);
        validateComponentReference(arg3, true);
    }

    /**
     * Simple injection with a List of components argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ComponentInjection(String componentId, List arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("argList", arg2, List.class);
        validateComponentReference(arg2, true);
    }

    /**
     * Simple injection with a Set of components argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ComponentInjection(String componentId, Set arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("argSet", arg2, Set.class);
    }

    /**
     * Simple injection with a Map of components argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public ComponentInjection(String componentId, Map arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("argMap", arg2, Map.class);
        validateComponentReference(arg2, true);
    }

    // a series of property setters for some complex test cases
    public void setComponentOne(ComponentTestInfo value) {
        setPropertyValue("componentOne", value);
        // this should be fully initialized now
        validateComponentReference(value, true);
    }

    public void setComponentTwo(ComponentTestInfo value) {
        setPropertyValue("componentTwo", value);
        // this should be fully initialized now
        validateComponentReference(value, true);
    }

    public void setComponentThree(ComponentTestInfo value) {
        setPropertyValue("componentThree", value);
        // this should be fully initialized now
        validateComponentReference(value, true);
    }

    public void setComponentList(List value) {
        setPropertyValue("componentList", value, List.class);
        validateComponentReference(value, true);
    }

    public void setComponentSet(Set value) {
        setPropertyValue("componentSet", value, Set.class);
        validateComponentReference(value, true);
    }

    public void setComponentMap(Map value) {
        setPropertyValue("componentMap", value, Map.class);
        validateComponentReference(value, true);
    }

    /**
     * Set a set of indirect component dependencies.
     *
     * @param value  The set of string component names.
     */
    public void setDependsOn(Set value) {
        props.put("dependsOn", value);
    }

    // default initialization checker method
    public void init() {
        // make sure we're marked as initialized.  This also broadcasts
        // an event that can be tracked.
        super.init();
        validateInitialization();
    }

    // default termination checker method
    public void destroy() {
        // make sure we're marked as destroyed.  This also broadcasts
        // an event that can be tracked.
        super.destroy();
        validateTermination();
    }

    protected void validateComponentReference(Object value, boolean state) {
        // null values are allowed
        if (value == null) {
            return;
        }
        if (value instanceof ComponentTestInfo) {
            if (state) {
                validateComponentInitialState((ComponentTestInfo)value);
            }
            else {
                validateComponentTerminationState((ComponentTestInfo)value);
            }
        }
        // recursive validation required
        else if (value instanceof List) {
            validateComponentReferences((Collection)value, state);
        }
        else if (value instanceof Set) {
            validateComponentReferences((Collection)value, state);
        }
        else if (value instanceof Map) {
            // we can have references both in the keys and the values,
            // so validate both sets.
            validateComponentReferences(((Map)value).keySet(), state);
            validateComponentReferences(((Map)value).entrySet(), state);
        }
    }

    protected void validateComponentReferences(Collection list, boolean state) {
        Iterator i = list.iterator();
        while (i.hasNext()) {
            validateComponentReference(i.next(), state);
        }
    }


    /**
     * Validate that a component is in correct initial state.
     *
     * @param c      The component to validate (can be null).
     */
    protected void validateComponentInitialState(ComponentTestInfo c) {
        if (c != null) {
            AssertionService.assertTrue(this, "Component " + c.getComponentId() + " is not initialized", c.isInitialized());
        }
    }

    /**
     * Validate that a component is in correct initial state.
     *
     * @param c      The component to validate (can be null).
     */
    protected void validateComponentTerminationState(ComponentTestInfo c) {
        if (c != null) {
            AssertionService.assertFalse(this, "Component " + c.getComponentId() + " is already destroyed", c.isDestroyed());
        }
    }

    protected void processTerminationReferences(Hashtable t) {
        Enumeration e = t.elements();
        while (e.hasMoreElements()) {
            // go through the table of references looking for the
            // component references to validate
            ValueDescriptor v = (ValueDescriptor)e.nextElement();
            validateComponentReference(v.getValue(), false);
        }
    }


    /**
     * Validate the initialization state.  This include validating
     * and depends-on references.
     */
    protected void validateInitialization() {
        // validate the state of the depends on components
        Set dependsOn = (Set)props.get("dependsOn");
        // this is only necessary if we have a dependent set of properties.
        if (dependsOn != null) {
            Iterator i = dependsOn.iterator();
            while (i.hasNext()) {
                String id = (String)i.next();
                Object component = getDependentComponent(id);
                // this must be here
                AssertionService.assertNotNull(this, "dependent component " + id + " not found", component);
                // and validate the value
                validateComponentReference(component, true);
            }
        }
    }


    protected void validateTermination() {
        processTerminationReferences(arguments);
        processTerminationReferences(properties);
    }


    /**
     * Add a marker for an initialized dependent component.
     *
     * @param id       The component id of the component.
     * @param instance The component instance.
     */
    protected void addDependentComponent(String id, Object instance) {
        // add this to the static table
        dependentComponents.put(id, instance);
    }


    /**
     * Verify if a dependent component has been initialized.
     *
     * @param id     The id of the component of interest.
     *
     * @return true if this component has been completely initialized.
     */
    protected boolean checkDependentComponent(String id) {
        return dependentComponents.get(id) != null;
    }


    /**
     * Retrieve a dependent component by id.
     *
     * @param id     The target id.
     *
     * @return The matching component from the dependent list.
     */
    protected Object getDependentComponent(String id) {
        return dependentComponents.get(id);
    }
}


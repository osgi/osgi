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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.osgi.test.cases.blueprint.services.AssertionService;

public class BaseTestComponent implements ComponentTestInfo{
    // an incremental creation id used to distinguish between component instances.
    static int creationId = 0;
    // the component id
    protected String componentId = "comp1";
    // our underlying property bundle used for validation.
    protected Hashtable props = new Hashtable();
    // our injected constructor argument values
    protected Hashtable arguments = new Hashtable();
    // our injected property value values
    protected Hashtable properties = new Hashtable();


    protected BaseTestComponent() {
        this("comp1");
    }


    protected BaseTestComponent(String componentId) {
        AssertionService.assertNotNull(this, componentId, "Component id null");
        this.componentId = componentId;
        props.put(BEAN_ID, componentId);
        props.put(INIT_CALLED, Boolean.FALSE);
        props.put(DESTROY_CALLED, Boolean.FALSE);
        props.put(BEAN_INSTANCE, new Integer(creationId++));
        // the arguments and properties are maintained in the snapshot tree
        props.put(BEAN_ARGUMENTS, arguments);
        props.put(BEAN_PROPERTIES, properties);
        AssertionService.sendEvent(this, AssertionService.BEAN_CREATED);
    }

    /**
     * Return a component identifier string used for determining
     * which component might be in error.
     *
     * @return The String id of the component.
     */
    public String getComponentId() {
        return componentId;
    }

    /**
     * Returns the unique component identifier for an
     * instance.  This is used to distinquish equality for some
     * injection components.
     *
     * @return The unique component identifer.
     */
    public int getComponentInstance() {
        Integer id = (Integer)props.get(BEAN_INSTANCE);
        // we should always have one of these
        if (id == null) {
            return 0;
        }
        return id.intValue();
    }


    /**
     * Returns a set of properties related to this created component.
     *
     * @return A dictionary with properties related to this component.  All
     *         components should return a standard property set, but specific
     *         ones migh return additional information.
     */
    public Dictionary getComponentProperties() {
        // this returns a snapshot of the component properties at the
        // time this is requested.
        Dictionary copy = (Dictionary)props.clone();
        // we also snapshot the arguments and properties collections to get an
        // immutable version
        copy.put(BEAN_ARGUMENTS, arguments.clone());
        copy.put(BEAN_PROPERTIES, properties.clone());
        return copy;
    }


    /**
     * Retrieve a property for a given name from a component.
     *
     * @param propName The property name.
     *
     * @return Any object associated with that property.
     */
    public Object getProperty(String propName) {
        return props.get(propName);
    }

    /**
     * Add a component value to our property set.
     *
     * @param value  The value object to add.
     */
    protected void setPropertyValue(String name, ValueDescriptor value) {
        properties.put(name, value);
    }


    /**
     * Add a component value to our argument set.
     *
     * @param value  The value object to add.
     */
    protected void setArgumentValue(String name, ValueDescriptor value) {
        arguments.put(name, value);
    }

    /**
     * Process a property set event using a test component reference
     *
     * @param name   The name of the property.
     * @param value  The value associated with the property.
     */
    protected void setPropertyValue(String name, ComponentTestInfo value) {
        setPropertyValue(name, new ComponentDescriptor(name, value));
    }

    /**
     * Process a property set event, recording the new property.
     *
     * @param name   The name of the property.
     * @param value  The value associated with the property.
     * @param clz    The class of the property type.  This is used to distinguish between
     *               primitive types vs. wrappered types, since the values are stored in
     *               wrappered form.
     */
    protected void setPropertyValue(String name, Object value, Class clz) {
        setPropertyValue(name, new StringValueDescriptor(name, value, clz));
    }

    /**
     * Process a constructor argument injection event, recording the new argument value.
     *
     * @param name   The name of the property.
     * @param value  The value associated with the property.
     * @param clz    The class of the argument type.  This is used to distinguish between
     *               primitive types vs. wrappered types, since the values are stored in
     *               wrappered form.
     */
    protected void setArgumentValue(String name, Object value, Class clz) {
        setArgumentValue(name, new StringValueDescriptor(name, value, clz));
    }

    /**
     * Process a property set event, recording the new property.
     *
     * @param name   The name of the property.
     * @param value  The value associated with the property.
     */
    protected void setPropertyValue(String name, Object value) {
        setPropertyValue(name, value, null);
    }

    protected void setPropertyValue(String name, boolean v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    protected void setPropertyValue(String name, byte v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    protected void setPropertyValue(String name, char v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    protected void setPropertyValue(String name, short v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    protected void setPropertyValue(String name, int v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    protected void setPropertyValue(String name, long v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    protected void setPropertyValue(String name, double v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    protected void setPropertyValue(String name, float v) {
        setPropertyValue(name, new StringValueDescriptor(name, v));
    }

    public ValueDescriptor getPropertyValue(String name) {
        return (ValueDescriptor)properties.get(name);
    }

    /**
     * Process a constructor argument injection event, recording the new argument value.
     *
     * @param name   The name of the property.
     * @param value  The value associated with the property.
     */
    protected void setArgumentValue(String name, ComponentTestInfo value) {
        setArgumentValue(name, new ComponentDescriptor(name, value));
    }

    /**
     * Process a constructor argument injection event, recording the new argument value.
     *
     * @param name   The name of the property.
     * @param value  The value associated with the property.
     */
    protected void setArgumentValue(String name, Object value) {
        setArgumentValue(name, value, null);
    }

    protected void setArgumentValue(String name, boolean v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    protected void setArgumentValue(String name, byte v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    protected void setArgumentValue(String name, char v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    protected void setArgumentValue(String name, short v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    protected void setArgumentValue(String name, int v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    protected void setArgumentValue(String name, long v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    protected void setArgumentValue(String name, double v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    protected void setArgumentValue(String name, float v) {
        setArgumentValue(name, new StringValueDescriptor(name, v));
    }

    public ValueDescriptor getArgumentValue(String name) {
        return (ValueDescriptor)arguments.get(name);
    }

    /**
     * Identify a component by its id.
     *
     * @return The component id name.
     */
    public String toString() {
        return "Component:" + componentId + "|Args:" + toString(arguments) + "|Props:" + toString(properties);
    }

    private String toString(Hashtable properties) {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        Iterator i = properties.keySet().iterator();
        while (i.hasNext()) {
            String name = (String)i.next();
            buf.append(name).append(" = ");
            ValueDescriptor desc = (ValueDescriptor) properties.get(name);
            if (desc.getValue() == this) {
                buf.append("<Component:").append(componentId).append(">");
            } else {
                buf.append(desc);
            }
            if (i.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    /**
     * Standard init method...used to broadcast the init event.
     */
    public void init() {
        props.put(INIT_CALLED, Boolean.TRUE);
        AssertionService.sendEvent(this, AssertionService.BEAN_INIT_METHOD);
    }


    /**
     * Standard init method...used to broadcast the destroy event.
     */
    public void destroy() {
        props.put(DESTROY_CALLED, Boolean.TRUE);
        AssertionService.sendEvent(this, AssertionService.BEAN_DESTROY_METHOD);
    }


    /**
     * Check the initialization state for a component.  This is generally
     * called by one component's init() method to verifify that a
     * component it references is in the correct state.
     *
     * @return true if the component has been completely initialized, false if
     *         the call to the init method is still pending.
     */
    public boolean isInitialized() {
        return getProperty(INIT_CALLED) == Boolean.TRUE;
    }


    /**
     * Check the destroy state of a component.  This is normally called
     * from the destroy() method of another object to verify that
     * component termination is proceeding in the correct order.
     *
     * @return true if the component has already been destroyed.  false if the
     *         method is still pending termination.  Note that false is the
     *         successful return value here.
     */
    public boolean isDestroyed() {
        return getProperty(DESTROY_CALLED) == Boolean.TRUE;
    }


    /**
     * Used when this component is an inner component..
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseTestComponent)) return false;
        BaseTestComponent obj = (BaseTestComponent)o;
        if (!this.componentId.equals(obj.componentId)) return false;
        if (compareHashtables(this.properties, obj.properties)
                && compareHashtables(this.arguments, obj.arguments)) return true;
        else return false;

    }

    private boolean compareHashtables(Hashtable h1, Hashtable h2){
        if (h1==null && h2==null) return true;
        else if (h1==null && h2!=null) return false;
        else if (h1!=null && h2==null) return false;
        else {
            return h1.equals(h2);
        }

    }

    /**
     * Override hashCode() because we overrode the equals()
     */
    public int hashCode() {
        int r = 17;
        r = 19*r + this.componentId.hashCode();

        Set keys;
        Iterator it;
        if (this.properties!=null){
            keys = this.properties.keySet();
            it = keys.iterator();
            while(it.hasNext()){
                r = 19*r + this.properties.get(it.next()).hashCode();
            }
        }
        if (this.arguments!=null){
            keys = this.arguments.keySet();
            it = keys.iterator();
            while(it.hasNext()){
                r = 19*r + this.arguments.get(it.next()).hashCode();
            }
        }
        return r;
    }
}


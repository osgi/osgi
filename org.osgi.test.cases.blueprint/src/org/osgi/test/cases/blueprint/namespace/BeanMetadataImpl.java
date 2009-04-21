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

package org.osgi.test.cases.blueprint.namespace;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Metadata;


import java.util.List;

/**
 * Metadata for a Bean Component.
 *
 * This class describes a <code>bean</code> element.
 */
public class BeanMetadataImpl extends ComponentMetadataImpl implements BeanMetadata {
    // the component class name
    private String className;
    private Class runtimeClass;
    private String initMethod;
    private String destroyMethod;
    private String scope;
    private boolean lazy;
    private List properties;
    private List arguments;
    private Metadata factoryComponent;
    private String factoryMethod;
    private List dependencies;

    public BeanMetadataImpl() {
        this((String)null);
    }

    public BeanMetadataImpl(String name) {
        super(name);
        // create a property and argument list
        properties = new ArrayList();
        arguments  = new ArrayList();
        dependencies = new ArrayList();
        scope = SCOPE_SINGLETON;
    }


    public BeanMetadataImpl(BeanMetadata source) {
        super(source);
        className = source.getClassName();
        runtimeClass = source.getRuntimeClass();
        initMethod = source.getInitMethodName();
        destroyMethod = source.getDestroyMethodName();
        factoryMethod = source.getFactoryMethodName();
        scope = source.getScope();
        lazy = source.isLazyInit();
        arguments = new ArrayList();
        // we need to deep copy this collection to replace with our own version
        Iterator argumentSource = source.getArguments().iterator();
        while (argumentSource.hasNext()) {
            arguments.add(new BeanArgumentImpl((BeanArgument)argumentSource.next()));
        }
        properties = new ArrayList();
        // we need to deep copy this collection to replace with our own version
        Iterator propertySource = source.getProperties().iterator();
        while (propertySource.hasNext()) {
            properties.add(new BeanPropertyImpl((BeanProperty)propertySource.next()));
        }
        factoryComponent = NamespaceUtil.cloneMetadata(source.getFactoryComponent());

        dependencies = new ArrayList();
        dependencies.addAll(source.getExplicitDependencies());
    }


    /**
     * The constructor injection metadata for this component.
     *
     * @return the constructor injection metadata. This is guaranteed to be
     * non-null and will refer to the default constructor if no explicit
     * constructor injection was specified for the component.
     */
    public List getArguments() {
        return arguments;
    }


    /**
     * A couple of convenience methods to make components
     * easier to construct.
     *
     * @param spec   The Parameter spect to add.
     */
    public void addArgument(BeanArgument spec) {
		arguments.add(spec);
	}

    /**
     * convenience method for String-based values
     *
     * @param value  The string source value.
     * @param index  The index position
     */
	public void addArgument(String value, int index) {
        BeanArgumentImpl spec = new BeanArgumentImpl();
        spec.setIndex(index);
        spec.setValue(new ValueMetadataImpl(value));
	}


    /**
     * The name of the class type specified for this component.
     *
     * @return the name of the component class. If no class was specified
     * in the component definition (because the a factory component is used
     * instead) then this method will return null.
     */
    public String getClassName() {
        return className;
    }

    public void setClassName(String name) {
        this.className = name;
    }

    public Class getRuntimeClass() {
        return runtimeClass;
    }

    public void setRuntimeClass(Class c) {
        runtimeClass = c;
    }


    /**
     * The name of the init method specified for this component, if any.
     *
     * @return the method name of the specified init method, or null if
     * no init method was specified.
     */
    public String getInitMethodName() {
        return initMethod;
    }

    public void setInitMethodName(String name) {
        this.initMethod = name;
    }


    /**
     * Is this component to be lazily instantiated?
     *
     * @return true, iff this component definition specifies lazy
     * instantiation.
     */
    public boolean isLazyInit() {
        return lazy;
    }

    public void setLazyInit(boolean lazy) {
        this.lazy = lazy;
    }


    /**
     * The name of the destroy method specified for this component, if any.
     *
     * @return the method name of the specified destroy method, or null if no
     * destroy method was specified.
     */
    public String getDestroyMethodName() {
        return destroyMethod;
    }


    public void setDestroyMethodName(String name) {
        destroyMethod = name;
    }

    /**
     * The component instance on which to invoke the factory method (if specified).
     *
     * @return when a factory method and factory component has been specified for this
     * component, this operation returns the metadata specifying the component on which
     * the factory method is to be invoked. When no factory component has been specified
     * this operation will return null. A return value of null with a non-null factory method
     * indicates that the factory method should be invoked as a static method on the
     * component class itself.
     */
    public Metadata getFactoryComponent() {
        return factoryComponent;
    }

    public void setFactoryComponent(Metadata m) {
        factoryComponent = m;
    }

    /**
     * The property injection metadata for this component.
     *
     * @return an immutable collection of PropertyInjectionMetadata, with one entry for each property to be injected. If
     * no property injection was specified for this component then an empty collection
     * will be returned.
     *
     */
    public List getProperties() {
        return properties;
    }

    public void addProperty(BeanProperty p) {
        properties.add(p);
    }

    public void replaceProperty(BeanProperty p) {
        String name = p.getName();
        // remove any matching item and replace with the new one
        Iterator i = properties.iterator();
        while (i.hasNext()) {
            BeanProperty current = (BeanProperty)i.next();
            if (current.getName().equals(name)) {
                i.remove();
                break;
            }
        }
        properties.add(p);
    }

    public void addProperty(String name, Metadata value) {
        addProperty(new BeanPropertyImpl(name, value));
    }

    public void addProperty(String name, String value) {
        addProperty(new BeanPropertyImpl(name, new ValueMetadataImpl(value)));
    }

    /**
     * The metadata describing how to create the component instance by invoking a
     * method (as opposed to a constructor) if factory methods are used.
     *
     * @return the method injection metadata for the specified factory method, or null if no
     * factory method is used for this component.
     */
    public String getFactoryMethodName() {
        return factoryMethod;
    }

    public void setFactoryMethodName(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }


    /**
     * The specified scope for the component lifecycle.
     *
     * @return a String indicating the scope specified for the component.
     *
     * @see #SCOPE_SINGLETON
     * @see #SCOPE_PROTOTYPE
     * @see #SCOPE_BUNDLE
     */
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * The names of any components listed in a "depends-on" attribute for this
     * component.
     *
     * @return an immutable set of component names for components that we have explicitly
     * declared a dependency on, or an empty set if none.
     */
    public List/*<RefMetadata>*/ getExplicitDependencies() {
        return dependencies;
    }


    /**
     * Add a new dependency to the explicit list.
     *
     * @param name   The new dependency name.
     */
    public void addDependency(String name) {
        dependencies.add(new RefMetadataImpl(name));
    }
}



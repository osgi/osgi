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
import java.util.Iterator;
import java.util.List;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ConstructorInjectionMetadata;
import org.osgi.service.blueprint.reflect.LocalComponentMetadata;
import org.osgi.service.blueprint.reflect.MethodInjectionMetadata;
import org.osgi.service.blueprint.reflect.PropertyInjectionMetadata;
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
public class LocalComponentMetadataImpl extends ComponentMetadataImpl implements LocalComponentMetadata {
    // the constructor metadata impl information.
    private ConstructorInjectionMetadataImpl constructorInjection;
    // the component class name
    private String className;
    private String initMethod;
    private String destroyMethod;
    private String scope;
    private boolean lazy;
    private Collection propertyInjection;
    private Value factoryComponent;
    private MethodInjectionMetadata factoryMethod;

    public LocalComponentMetadataImpl() {
        this((String)null);
    }

    public LocalComponentMetadataImpl(String name) {
        super(name);
        // create a new one of these, even if empty
        propertyInjection = new ArrayList();
        scope = SCOPE_SINGLETON;
        // we always have something here
        constructorInjection = new ConstructorInjectionMetadataImpl();
    }


    public LocalComponentMetadataImpl(LocalComponentMetadata source) {
        super(source);
        constructorInjection = new ConstructorInjectionMetadataImpl(source.getConstructorInjectionMetadata());
        initMethod = source.getInitMethodName();
        destroyMethod = source.getDestroyMethodName();
        scope = source.getScope();
        lazy = source.isLazy();
        propertyInjection = new ArrayList();
        // we need to deep copy this collection to replace with our own version
        Iterator propertySource = source.getPropertyInjectionMetadata().iterator();
        while (propertySource.hasNext()) {
            propertyInjection.add(new PropertyInjectionMetadataImpl((PropertyInjectionMetadata)propertySource.next()));
        }
        factoryComponent = NamespaceUtil.cloneValue(source.getFactoryComponent());
    }


    /**
     * The constructor injection metadata for this component.
     *
     * @return the constructor injection metadata. This is guaranteed to be
     * non-null and will refer to the default constructor if no explicit
     * constructor injection was specified for the component.
     */
    public ConstructorInjectionMetadata getConstructorInjectionMetadata() {
        return constructorInjection;
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
    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
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
    public Value getFactoryComponent() {
        return factoryComponent;
    }

    public void setFactoryComponent(Value m) {
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
    public Collection getPropertyInjectionMetadata() {
        return propertyInjection;
    }

    public void addPropertyInjectionMetadata(PropertyInjectionMetadata m) {
        propertyInjection.add(m);
    }



    /**
     * The metadata describing how to create the component instance by invoking a
     * method (as opposed to a constructor) if factory methods are used.
     *
     * @return the method injection metadata for the specified factory method, or null if no
     * factory method is used for this component.
     */
    public MethodInjectionMetadata getFactoryMethodMetadata() {
        return factoryMethod;
    }

    public void setFactoryMethodMetadata(MethodInjectionMetadata factoryMethod) {
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
}


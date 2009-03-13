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

import org.osgi.service.blueprint.reflect.ComponentValue;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.LocalComponentMetadata;
import org.osgi.service.blueprint.reflect.MethodInjectionMetadata;
import org.osgi.service.blueprint.reflect.Value;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

/**
 * A wrapper for component definition metadata.
 */
public class LocalComponent extends Assert implements TestComponentMetadata {
    // a property name to match on (only used if not-null)
    protected String name;
    // the component class name (can double as a static factory class name)
    protected String className;
    // a potential init-method name
    protected String initMethodName;
    // a potential destroy-method name
    protected String destroyMethodName;
    // a potential list of constructor/factory parameters to validate
    // NOTE:  This parameter list applies to the factory method as well.
    // how it gets applied depends on whether a factory method name has been
    // set
    protected TestParameter[] parms;
    // a potential list of property injection values to check
    protected TestProperty[] props;
    // a potential list of dependsOn components to process
    protected String[] dependsOn;
    // the lazy attribute
    protected boolean isLazy;
    // the scope attribute
    protected String scope;
    // a factory component (optional)
    protected TestValue factoryComponent;
    // the name of the factory method to invoke.
    protected String factoryMethod;
    // the factory injection metadata

    public LocalComponent(Class classType) {
        this(null, classType, null, null, null, null, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    public LocalComponent(Class classType, TestParameter[] parms, TestProperty[] props) {
        this(null, classType, null, null, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    public LocalComponent(String name, Class classType, TestParameter[] parms, TestProperty[] props) {
        this(name, classType, null, null, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    public LocalComponent(String name, Class classType, String initMethodName, String destroyMethodName, TestParameter[] parms, TestProperty[] props) {
        this(name, classType, initMethodName, destroyMethodName, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    public LocalComponent(String name, Class classType, String initMethodName, String destroyMethodName, TestParameter[] parms, TestProperty[] props, String[] dependsOn, boolean isLazy, String scope) {
        this.name = name;
        if (classType != null) {
            this.className = classType.getName();
        }
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
        this.parms = parms;
        this.props = props;
        this.dependsOn = dependsOn;
        this.isLazy = isLazy;
        this.scope = scope;
        // make sure we get the default set
        if (this.scope == null) {
            this.scope = LocalComponentMetadata.SCOPE_SINGLETON;
        }
        // these generally get set post-construction
        this.factoryComponent = null;
        this.factoryMethod = null;
    }

    /**
     * Set the factory component name if this is using
     * the instance factory pattern.
     *
     * @param name   The name of the target factory.
     */
    public void setFactoryComponent(TestValue factory) {
        factoryComponent = factory;
    }


    /**
     * Set the name of the factory method.
     *
     * @param name   The factory method name.
     */
    public void setFactoryMethod(String name) {
        this.factoryMethod = name;
    }


    /**
     * Validate a the contained component meta data against an actual instance
     *
     * @param moduleMetadata
     *               The module metadata wrapper (needed for validating
     *               embedded types)
     * @param componentMeta
     *               The metadata target we validate against.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, ComponentMetadata componentMeta) throws Exception {
        assertTrue("Component type mismatch", componentMeta instanceof LocalComponentMetadata);
        LocalComponentMetadata meta = (LocalComponentMetadata)componentMeta;
        if (name != null) {
            if (name.equals("*")) {
                assertNotNull("Missing generated component name", meta.getName());
            }
            else {
                assertEquals("Component name mismatch", name, meta.getName());
            }
        }
        else {
            assertNull("non-null component name for inner component", meta.getName());
        }
        assertEquals("Component " + meta.getName() + " class name mismatch", className, meta.getClassName());
        assertEquals("Component " + meta.getName() + " init-method mismatch", initMethodName, meta.getInitMethodName());
        assertEquals("Component " + meta.getName() + " destroy-method mismatch", initMethodName, meta.getDestroyMethodName());
        // this is either instantiatied via a factory or directly
        if (factoryMethod != null) {
            MethodInjectionMetadata factoryMeta = meta.getFactoryMethodMetadata();
            assertNotNull("Component " + meta.getName() + " factory metadata expected", factoryMeta);
            assertEquals("Component " + meta.getName() + " factory method mismatch ", factoryMethod, factoryMeta.getName());
            // if we have parms to validate, check those now
            if (parms != null) {
                moduleMetadata.validateConstructorParameters(meta, factoryMeta.getParameterSpecifications(), parms);
            }
            // and a potential factory component
            if (factoryComponent != null) {
                Value factory = meta.getFactoryComponent();
                assertNotNull("Component " + meta.getName() + "factory component definition expected", factory);
                // validate that component information
                factoryComponent.validate(moduleMetadata, factory);
            }
        }
        else {
            // directly created, so we might have parms to validate
            if (parms != null) {
                moduleMetadata.validateConstructorParameters(meta, meta.getConstructorInjectionMetadata().getParameterSpecifications(), parms);
            }
        }

        // validate the property definitions
        if (props != null) {
            moduleMetadata.validatePropertyMetadata(meta, props);
        }

        if (dependsOn != null) {
            Set test = new HashSet();
            for (int i = 0; i < dependsOn.length; i++) {
                test.add(dependsOn[i]);
            }
            assertEquals("Depends on definition", test, meta.getExplicitDependencies());
        }

        assertEquals("Component lazy attribute", isLazy, meta.isLazy());
        assertEquals("Component scope", scope, meta.getScope());
    }


    /**
     * do a comparison between a real metadata item and our test validator.
     * This is used primarily to locate specific values in the different
     * CollectionValues.
     *
     * @param v      The target value item.
     *
     * @return True if this can be considered a match, false for any mismatch.
     */
    public boolean matches(ComponentMetadata componentMeta) {
        // we only handle local component references.
        if (!(componentMeta instanceof LocalComponentMetadata)) {
            return false;
        }

        LocalComponentMetadata meta = (LocalComponentMetadata)componentMeta;
        // if we have a wildcard name, then we skip the name comparison.
        if (name != null && !name.equals("*")) {
            return name.equals(meta.getName());
        }
        // compare on the class name if we have one (this might be a factory component)
        if (className != null) {
            if (meta.getClassName() == null) {
                return false;
            }
            if (!className.equals(meta.getClassName())) {
                return false;
            }
        }
        else {
            if (meta.getClassName() != null) {
                return false;
            }
        }
        // NB:  These are a bit hard to match on, so if multiple inner components are used for
        // Set or Map collections, it might be necessary to use different component classes.
        return true;
    }


    /**
     * Retrieve the name for this component.  Used for validation
     * purposes.
     *
     * @return The String name of the component.  Returns null
     *         if no name has been provided.
     */
    public String getName() {
        return name;
    }
}


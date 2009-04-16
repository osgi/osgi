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

package org.osgi.test.cases.blueprint.framework;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.LocalComponentMetadata;

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
    protected TestValue factoryTestComponentValue;
    // the name of the factory method to invoke.
    protected String factoryMethod;
    // the factory injection metadata

    // indicated the component is created directly from class
    public LocalComponent(String name, Class classType, TestParameter[] parms, TestProperty[] props) {
        this(name, classType, null, null, null, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    // indicated the component is created by instance factory
    public LocalComponent(String name, String factoryMethodName, TestParameter[] parms, TestProperty[] props) {
        this(name, null, factoryMethodName, null, null, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    // indicated the component is created by static factory
    public LocalComponent(String name, Class classType, String factoryMethodName, TestParameter[] parms, TestProperty[] props) {
        this(name, classType, factoryMethodName, null, null, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    // indicated the component is inner component
    public LocalComponent(Class classType, String factoryMethodName, TestParameter[] parms, TestProperty[] props) {
        this(null, classType, factoryMethodName, null, null, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    public LocalComponent(String name, Class classType, String initMethodName, String destroyMethodName, TestParameter[] parms, TestProperty[] props) {
        this(name, classType, null, initMethodName, destroyMethodName, parms, props, null, false, LocalComponentMetadata.SCOPE_SINGLETON);
    }

    public LocalComponent(String name, Class classType, String factoryMethodName, String initMethodName, String destroyMethodName, TestParameter[] parms, TestProperty[] props, String[] dependsOn, boolean isLazy, String scope) {
        this.name = name;
        if (classType != null) {
            this.className = classType.getName();
        }
        this.factoryMethod = factoryMethodName;
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
        this.factoryTestComponentValue = null;

    }

    /**
     * Set the factory component name if this is using
     * the instance factory pattern.
     *
     * @param name   The name of the target factory.
     */
    public void setFactoryComponent(TestValue factory) {
        factoryTestComponentValue = factory;
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

        // three ways to create a component
        if (className != null && factoryMethod == null){
            // directly created from class
            assertEquals("Component " + meta.getName() + " class name mismatch", className, meta.getClassName());
            assertNull("Component " + meta.getName() + " factory metadata is not null", meta.getFactoryMethodMetadata());
        }else if (className != null && factoryMethod != null){
            // by static factory
            moduleMetadata.validateFactoryMetadata(meta, factoryMethod, className, null);
        }else if (className == null && factoryMethod != null){
            // by instance factory
            moduleMetadata.validateFactoryMetadata(meta, factoryMethod, null, factoryTestComponentValue);
        }else{
            // error status
            fail("The test data for Component " + meta.getName() +" instantiated incorrectly, both class type and factory method name are null.");
        }



        // validate the parameters
        if (parms != null) {
            moduleMetadata.validateConstructorMetadata(meta, parms);
        }

//        // this is either instantiatied via a factory or directly
//        if (factoryMethod != null) {
//            MethodInjectionMetadata factoryMeta = meta.getFactoryMethodMetadata();
//            assertNotNull("Component " + meta.getName() + " factory metadata expected", factoryMeta);
//            assertEquals("Component " + meta.getName() + " factory method mismatch ", factoryMethod, factoryMeta.getName());
//            // if we have parms to validate, check those now
//            if (parms != null) {
//                moduleMetadata.validateConstructorParameters(meta, factoryMeta.getParameterSpecifications(), parms);
//            }
//            // and a potential factory component
//            if (factoryComponent != null) {
//                Value factory = meta.getFactoryComponent();
//                assertNotNull("Component " + meta.getName() + "factory component definition expected", factory);
//                // validate that component information
//                factoryComponent.validate(moduleMetadata, factory);
//            }
//        }
//        else {
//            // directly created, so we might have parms to validate
//            if (parms != null) {
//                moduleMetadata.validateConstructorParameters(meta, meta.getConstructorInjectionMetadata().getParameterSpecifications(), parms);
//            }
//        }

        // validate the property definitions
        if (props != null) {
            moduleMetadata.validatePropertyMetadata(meta, props);
        }

        assertEquals("Component " + meta.getName() + " init-method mismatch", initMethodName, meta.getInitMethodName());
        assertEquals("Component " + meta.getName() + " destroy-method mismatch", initMethodName, meta.getDestroyMethodName());

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


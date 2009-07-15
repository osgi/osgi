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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.Target;

/**
 * A wrapper for component definition metadata.
 */
public class BeanComponent extends Assert implements TestComponentMetadata {
    // a property name to match on (only used if not-null)
    protected String name;
    // the component class name (can double as a static factory class name)
    protected String className;
    // a potential init-method name
    protected String initMethodName;
    // a potential destroy-method name
    protected String destroyMethodName;
    // a potential list of constructor/factory arguments to validate
    protected TestArgument[] parms;
    // a potential list of property injection values to check
    protected TestProperty[] props;
    // a potential list of dependsOn components to process
    protected String[] dependsOn;
    // the activation attribute
    protected int activation;
    // the scope attribute
    protected String scope;
    // a factory component (optional)
    protected TestValue factoryComponent;
    // the name of the factory method to invoke.
    protected String factoryMethod;
    // the factory injection metadata

    // indicated the component is created directly from class
    public BeanComponent(String name, Class classType, TestArgument[] parms, TestProperty[] props) {
        this(name, classType, null, null, null, parms, props, null, BeanMetadata.ACTIVATION_EAGER, null);
    }

    // indicated the component is created by instance factory
    public BeanComponent(String name, String factoryMethodName, TestArgument[] parms, TestProperty[] props) {
        this(name, null, factoryMethodName, null, null, parms, props, null, BeanMetadata.ACTIVATION_EAGER, null);
    }

    // indicated the component is created by static factory
    public BeanComponent(String name, Class classType, String factoryMethodName, TestArgument[] parms, TestProperty[] props) {
        this(name, classType, factoryMethodName, null, null, parms, props, null, BeanMetadata.ACTIVATION_EAGER, null);
    }

    // indicated the component is inner component
    public BeanComponent(Class classType, String factoryMethodName, TestArgument[] parms, TestProperty[] props) {
        this(null, classType, factoryMethodName, null, null, parms, props, null, BeanMetadata.ACTIVATION_EAGER, null);
    }

    public BeanComponent(String name, Class classType, String initMethodName, String destroyMethodName, TestArgument[] parms, TestProperty[] props) {
        this(name, classType, null, initMethodName, destroyMethodName, parms, props, null, BeanMetadata.ACTIVATION_EAGER, null);
    }

    public BeanComponent(String name, Class classType, String factoryMethodName, String initMethodName, String destroyMethodName, TestArgument[] parms, TestProperty[] props, String[] dependsOn, int activation, String scope) {
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
        this.activation = activation;
        this.scope = scope;
        // make sure we get the default set
        if (this.scope == null) {
            this.scope = BeanMetadata.SCOPE_SINGLETON;
        }
        // these generally get set post-construction
        this.factoryComponent = null;

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
     * Validate a the contained component meta data against an actual instance
     *
     * @param blueprintMetadata
     *               The module metadata wrapper (needed for validating
     *               embedded types)
     * @param componentMeta
     *               The metadata target we validate against.
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, ComponentMetadata componentMeta) throws Exception {
        assertTrue("Component type mismatch", componentMeta instanceof BeanMetadata);
        BeanMetadata meta = (BeanMetadata)componentMeta;

        if (name != null) {
            if (name.equals("*")) {
                assertNotNull("Missing generated component name", meta.getId());
            }
            else {
                assertEquals("Component name mismatch", name, meta.getId());
            }
        }
        else {
            assertNull("non-null component name for inner component", meta.getId());
        }

        // three ways to create a component
        if (className != null && factoryMethod == null){
            // directly created from class
            assertEquals("Component " + meta.getId() + " class name mismatch", className, meta.getClassName());
        }else if (className != null && factoryMethod != null){
            // by static factory
            blueprintMetadata.validateFactoryMetadata(meta, factoryMethod, className, null);
        }else if (className == null && factoryMethod != null){
            // by instance factory
            blueprintMetadata.validateFactoryMetadata(meta, factoryMethod, null, factoryComponent);
        }else{
            // error status
            fail("The test data for Component " + meta.getId() +" instantiated incorrectly, both class type and factory method name are null.");
        }

        // validate the arguments
        if (parms != null) {
            blueprintMetadata.validateArgumentMetadata(meta, parms);
        }

        // validate the property definitions
        if (props != null) {
            blueprintMetadata.validatePropertyMetadata(meta, props);
        }

        // this is either instantiatied via a factory or directly
        assertEquals("Component " + meta.getId() + " factory method mismatch ", factoryMethod, meta.getFactoryMethod());
        // and a potential factory component
        if (factoryComponent != null) {
            Target factory = meta.getFactoryComponent();
            assertNotNull("Component " + meta.getId() + "factory component definition expected", factory);
            // validate that component information
            factoryComponent.validate(blueprintMetadata, factory);
        }

        assertEquals("Component " + meta.getId() + " init-method mismatch", initMethodName, meta.getInitMethod());
        assertEquals("Component " + meta.getId() + " destroy-method mismatch", destroyMethodName, meta.getDestroyMethod());

        if (dependsOn != null) {
            List test = new ArrayList();
            for (int i = 0; i < dependsOn.length; i++) {
                test.add(dependsOn[i]);
            }
            assertEquals("Depends on definition", test, meta.getDependsOn());
        }

        assertEquals("Component activation attribute", activation, meta.getActivation());
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
        if (!(componentMeta instanceof BeanMetadata)) {
            return false;
        }

        BeanMetadata meta = (BeanMetadata)componentMeta;
        // if we have a wildcard name, then we skip the name comparison.
        if (name != null && !name.equals("*")) {
            return name.equals(meta.getId());
        }

        // TODO:  add class validation also

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
    public String getId() {
        return name;
    }
}


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
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.service.blueprint.reflect.Target;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * A wrapper around the published BlueprintContainer service for a managed
 * bundle.  This is used by the different validators, initializers,
 * and terminators to perform metadata-related operations.
 */
public class BlueprintMetadata extends Assert implements TestValidator, TestCleanup {
    // the bundle context we're running in
    protected BundleContext testContext;
    // the bundle we're validating for
    protected Bundle targetBundle;
    // the resolved service reference to the target module context.
    protected ServiceReference contextRef;
    // the actual module context service
    protected BlueprintContainer targetBlueprintContainer;


    /**
     * Create a metadata item for the given bundle.
     *
     * @param testContext    The text BundleContext (needed for services operations).
     * @param targetBundle   The target test bundle.
     */
    public BlueprintMetadata(BundleContext testContext, Bundle targetBundle) {
        this.testContext = testContext;
        this.targetBundle = targetBundle;
    }


    /**
     * Provide access to the bundle that owns the metadata.
     *
     * @return The associated bundle.
     */
    public Bundle getTargetBundle() {
        return targetBundle;
    }


    /**
     * Perform any additional validation checks at the end of a test phase.
     * This can perform any validation action needed beyond just
     * event verification.  One good use is to ensure that specific
     * services are actually in the services registry or validating
     * the service properties.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void validate(BundleContext testContext) throws Exception {
        // just get the context at this point
        getBlueprintContainer();
    }

    /**
     * Retrieve the context for the target bundle.
     */
    protected void getBlueprintContainer() throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences("org.osgi.service.blueprint.container.BlueprintContainer",
            "(osgi.blueprint.container.symbolicname=" + targetBundle.getSymbolicName() + ")");
        assertNotNull("No BlueprintContainer located for bundle " + targetBundle.getSymbolicName(), refs);
        assertEquals("Bundle mismatch for BlueprintContainer instance.", targetBundle, refs[0].getBundle());

        // save the module context reference
        contextRef = refs[0];
        targetBlueprintContainer = (BlueprintContainer)testContext.getService(contextRef);
    }


    /**
     * Retrieve the associated module context.  Some validators
     * require direct access to this, so allow it to be
     * retrieved.
     *
     * @return The bundle published module context.
     * @exception Exception
     */
    public BlueprintContainer getTargetBlueprintContainer() throws Exception {
        getBlueprintContainer();
        return targetBlueprintContainer;
    }


    /**
     * Retrieve a named component from module context.
     *
     * @param componentName
     * @param className
     */
    public Object getComponent(String componentName) throws Exception {
        // ensure we have a good context first
        getBlueprintContainer();
        try {
            // get the object instance
            Object component = targetBlueprintContainer.getComponentInstance(componentName);
            assertNotNull("Component " + componentName + " not found in BlueprintContainer for " + targetBundle.getSymbolicName(), component);
            return component;
        } catch (NoSuchComponentException e) {
            fail("Component " + componentName + " not created for bundle " + targetBundle.getSymbolicName());
        }
        return null;
    }


    /**
     * Retrieve a matching component from module context.
     *
     * @param component The metadata information we're looking for.  We'll
     *                  accept the first one it agrees is a match.
     *
     * @return The matching component metadata
     * @exception Exception
     */
    public ComponentMetadata getComponentMetadata(TestComponentMetadata target) throws Exception {
        // ensure we have a good context first
        getBlueprintContainer();
        try {
            Iterator componentNames = targetBlueprintContainer.getComponentIds().iterator();
            while (componentNames.hasNext()) {
                String name = (String)componentNames.next();

                // get the metadata instance for the next name and check for a match
                ComponentMetadata component = targetBlueprintContainer.getComponentMetadata(name);
                if (target.matches(component)) {
                    return component;
                }
            }
        } catch (NoSuchComponentException e) {
            fail("Component " + target.getId() + " not created for bundle " + targetBundle.getSymbolicName());
        }
        return null;
    }


    /**
     * Retrieve the metadata for a named component from module context.
     *
     * @param componentName
     */
    public ComponentMetadata getComponentMetadata(String componentName) throws Exception {
        // ensure we have a good context first
        getBlueprintContainer();
        try {
            // get the object instance
            ComponentMetadata component = targetBlueprintContainer.getComponentMetadata(componentName);
            assertNotNull("Component " + componentName + " not found in BlueprintContainer for " + targetBundle.getSymbolicName(), component);
            return component;
        } catch (NoSuchComponentException e) {
            fail("Component " + componentName + " not created for bundle " + targetBundle.getSymbolicName());
        }
        return null;
    }


    /**
     * Validate that the named component is of the correct class.
     *
     * @param component The component instance.
     * @param className The target class name.
     */
    public void validateComponentClass(Object component, Class clz) throws Exception {
        if (!clz.isInstance(component)) {
            fail("" + component + " not an instance of class " + clz.getName());
        }
    }


    /**
     * Validate the internal property bundle of a test component.
     *
     * @param component The component object.
     * @param props     The set of properties to compare against.
     */
    public void validateComponentProperties(Object component, Dictionary props) throws Exception {
        try {
            // this means we expect this to be a base component class.
            ComponentTestInfo base = (ComponentTestInfo)component;
            Dictionary componentProps = base.getComponentProperties();
            Enumeration keys = props.keys();
            // all of the expected properties need to be the same
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                assertEquals("property " + key + " in component " + component, props.get(key), componentProps.get(key));
            }
        } catch (ClassCastException e) {
            fail("Component " + component + " is not an instance of ComponentTestInfo");
        }
    }


    /**
     * Retrieve an internal component property
     *
     * @param component The registered component name.
     * @param name      The name of the target property.
     */
    public Object getComponentProperty(String componentId, String name) throws Exception {
        return getComponentProperty(getComponent(componentId), name);
    }


    /**
     * Retrieve an internal component property
     *
     * @param component The component object.
     * @param name      The name of the target property.
     */
    public Object getComponentProperty(Object component, String name) throws Exception {
        try {
            // this means we expect this to be a base component class.
            ComponentTestInfo base = (ComponentTestInfo)component;
            Dictionary componentProps = base.getComponentProperties();
            return componentProps.get(name);
        } catch (ClassCastException e) {
            fail("Component " + component + " is not an instance of ComponentTestInfo");
        }
        return null;
    }


    /**
     * Return the descriptor for an injected component property value.
     *
     * @param componentId
     *               The target componentId.
     * @param name   The name of the injected property.
     *
     * @return A TestComponentInfo object describing the property, or null if
     *         not found.
     * @exception Exception
     */
    public ValueDescriptor getComponentPropertyValue(String componentId, String propertyName) throws Exception {
        if (propertyName == null) {
            fail("Null property name for component " + componentId);
        }
        Dictionary properties = (Dictionary)getComponentProperty(componentId, ComponentTestInfo.BEAN_PROPERTIES);
        // if unable to resolve this, return null
        if (properties == null) {
            return null;
        }
        return (ValueDescriptor)properties.get(propertyName);
    }


    /**
     * Return the descriptor for an injected component argument value.
     *
     * @param componentId
     *               The target componentId.
     * @param name   The name of the injected argument.
     *
     * @return A TestComponentInfo object describing the argument, or null if
     *         not found.
     * @exception Exception
     */
    public ValueDescriptor getComponentArgumentValue(String componentId, String argumentName) throws Exception {
        Dictionary properties = (Dictionary)getComponentProperty(componentId, ComponentTestInfo.BEAN_ARGUMENTS);
        // if unable to resolve this, return null
        if (properties == null) {
            return null;
        }
        return (ValueDescriptor)properties.get(argumentName);
    }


    /**
     * Perform a full component validation operation.
     *
     * @param componentId
     *                  The target component id.
     * @param className The component class name.
     * @param props     The set of validation properties.
     */
    public void validateComponent(String componentId, Class componentClass, Dictionary props) throws Exception {
        Object component = getComponent(componentId);
        this.validateComponent(component, componentClass, props);
    }

    /**
     * Perform a full component validation operation.
     *
     * @param component
     *                  The target component
     * @param className The component class name.
     * @param props     The set of validation properties.
     */
    public void validateComponent(Object component, Class componentClass, Dictionary props) throws Exception {

        validateComponentClass(component, componentClass);
        if (props != null) {
            validateComponentProperties(component, props);
        }
    }


    /**
     * Validate that a component's metadata contains the expected lifecycle
     * components.
     *
     * @param componentId
     *                   The target component id.
     * @param className  The expected classname for the component.
     * @param initMethod The expected init method name.
     * @param destroyMethod
     *                   The expected destroy method name.
     *
     * @exception Exception
     */
    public void validateLifeCycle(String componentId, String className, String initMethod, String destroyMethod) throws Exception {
        // now validate the meta data is correct for the aliases.
        BeanMetadata meta = (BeanMetadata)targetBlueprintContainer.getComponentMetadata(componentId);
        assertEquals("Classname for component " + componentId, className, meta.getClassName());
        assertEquals("init-method for component " + componentId, initMethod, meta.getInitMethod());
        assertEquals("destroy-method for component " + componentId, destroyMethod, meta.getDestroyMethod());
    }

    /**
     * Validate the constructor parameters for a component against an expected set.
     *
     * @param componentId   The target component id.
     * @param expected      The set of expected items.
     *
     * @exception Exception
     */
    public void validateArgumentMetadata(String componentId, TestArgument[] expected) throws Exception {
        try {
            // now validate the meta data is correct for the parameters
            BeanMetadata meta = (BeanMetadata)targetBlueprintContainer.getComponentMetadata(componentId);
            this.validateArgumentMetadata(meta, expected);
        } catch (Throwable e) {
            // just allowing this to go past will result
            // about which component and property we're doing this on.  So
            // we'll throw a new assertion failure with the old one embedded.
            AssertionFailedError ee = new AssertionFailedError("Validation failure for component " + componentId);
            ee.initCause(e);
            throw ee;
        }
    }

    /**
     * Validate the constructor parameters for a component against an expected set.
     * @param meta  The metadata describing this component. This might be for an inner component.
     * @param expected
     * @throws Exception
     */
    public void validateArgumentMetadata(BeanMetadata meta, TestArgument[] expected) throws Exception {
        validateParameters(meta.getArguments(), expected);
    }

    /**
     * Validate the parameters for a component against an expected set.
     *
     * @param parmSpecs The parameter specs
     * @param expected  The set of expected items.
     *
     * @exception Exception
     */
    public void validateParameters(List parmSpecs, TestArgument[] expected) throws Exception {
        assertEquals("Mismatch in constructor parameter size", expected.length, parmSpecs.size());
        // validate each expected argment against the actual metadata for this constructor.
        for (int i = 0; i < expected.length; i++) {
            try {
                // explicitly set the index so it will compare.  The spec says this will
                // be set whether it was implicit or explicit, so enforce the ordering.
                // TODO:  Bugzilla 1155.  The index values are not getting set and -1
                // is returned, counter to what the spec says
//              expected[i].setIndex(i);
                expected[i].validate(this, (BeanArgument)parmSpecs.get(i));
            } catch (Throwable e) {
                // just allowing this to go past will result
                // about which component and property we're doing this on.  So
                // we'll throw a new assertion failure with the old one embedded.
                AssertionFailedError ee = new AssertionFailedError("Validation failure for component argument=" + i + ", error=" + e.getMessage());
                ee.initCause(e);
                throw ee;
            }
        }
    }

    /**
     * Validate the a partial list of constructor metadata for a component against an expected set.
     *
     * @param componentId
     *                 The target component id.
     * @param expected The set of expected items.  This need not be a full list,
     *                 but all parameters must have the index position set.
     *
     * @exception Exception
     */
    public void validatePartialConstructorMetadata(String componentId, TestArgument[] expected) throws Exception {
        // now validate the meta data is correct for the parameters
        BeanMetadata meta = (BeanMetadata)targetBlueprintContainer.getComponentMetadata(componentId);
        validatePartialConstructorMetadata(meta, expected);
    }

    /**
     * Validate the constructor metadata for a component against an expected set.
     *
     * @param meta     The metadata describing this component.  This might be for an
     *                 inner component.
     * @param expected The set of expected items.
     *
     * @exception Exception
     */
    public void validatePartialConstructorMetadata(BeanMetadata meta, TestArgument[] expected) throws Exception {
        validatePartialConstructorParameters(meta, meta.getArguments(), expected);
    }

    /**
     * Validate the constructor metadata for a component against an expected set.
     *
     * @param meta     The metadata describing this component.  This might be for an
     *                 inner component.
     * @param expected The set of expected items.
     *
     * @exception Exception
     */
    public void validatePartialConstructorParameters(BeanMetadata meta, List parms, TestArgument[] expected) throws Exception {
        assertEquals("Mismatch in constructor parameter size", expected.length, parms.size());
        // validate each expected argment against the actual metadata for this constructor.
        for (int i = 0; i < expected.length; i++) {
            try {
                // validate against the expected index position.
                expected[i].validate(this, (BeanArgument)parms.get(expected[i].getIndex()));
            } catch (Throwable e) {
                // just allowing this to go past will result
                // about which component and property we're doing this on.  So
                // we'll throw a new assertion failure with the old one embedded.
                AssertionFailedError ee = new AssertionFailedError("Validation failure for component argument=" + i + ", error=" + e.getMessage());
                ee.initCause(e);
                throw ee;
            }
        }
    }

    /**
     * Validate the method injection metadata for a factory definition against an expected set.
     *
     * @param componentId
     * @param factoryMethodName
     * @param staticFactoryClassName
     * @param factoryTestComponentValue
     * @param expected The set of expected items.
     *
     * @exception Exception
     */
    public void validateFactoryMetadata(String componentId, String factoryMethodName, String staticFactoryClassName, TestValue factoryTestComponentValue) throws Exception {
        // now validate the meta data is correct for the parameters
        BeanMetadata meta = (BeanMetadata)targetBlueprintContainer.getComponentMetadata(componentId);
        this.validateFactoryMetadata(meta, factoryMethodName, staticFactoryClassName, factoryTestComponentValue);
    }

    /**
     * Validate the method injection metadata
     *
     * @param meta  The metadata describing this component. This might be for an inner component.
     * @param factoryMethodName
     * @param staticFactoryClassName
     * @param factoryTestComponentValue
     * @throws Exception
     */
    public void validateFactoryMetadata(BeanMetadata meta, String factoryMethodName, String staticFactoryClassName, TestValue factoryTestComponentValue) throws Exception {
        // validate factory method name
        assertEquals("Factory method for component " + meta.getId(), factoryMethodName, meta.getFactoryMethod());

        // validate class name
        assertEquals("Component " + meta.getId() + " class name mismatch", staticFactoryClassName, meta.getClassName());

        // validate for instance factory
        if (factoryTestComponentValue != null) { //optional validate..
            Target factoryComponentValue = meta.getFactoryComponent();
            assertNotNull("Component " + meta.getId() + " factory component definition expected", factoryComponentValue);
            // for the named factories, we expect this to be a RefMetadata
            assertTrue("Factory RefMetadata expected", factoryComponentValue instanceof RefMetadata);
            // validate that component information
            factoryTestComponentValue.validate(this, factoryComponentValue);
        }

    }

    /**
     * Validate the property metadata for a component against an expected set.
     *
     * @param componentId   The target component id.
     * @param expected      The set of expected items.
     *
     * @exception Exception
     */
    public void validatePropertyMetadata(String componentId, TestProperty[] expected) throws Exception {
        // now validate the meta data is correct for the properties
        try {
            validatePropertyMetadata((BeanMetadata)targetBlueprintContainer.getComponentMetadata(componentId), expected);
        } catch (Throwable e) {
            // just allowing this to go past will result
            // about which component and property we're doing this on.  So
            // we'll throw a new assertion failure with the old one embedded.
            AssertionFailedError ee = new AssertionFailedError("Validation failure for component " + componentId);
            ee.initCause(e);
            throw ee;
        }
    }

    /**
     * Validate the property metadata for a component against an expected set.
     *
     * @param meta      The metadata describing this component. This might be for an inner component.
     * @param expected  The set of expected items.
     *
     * @exception Exception
     */
    public void validatePropertyMetadata(BeanMetadata meta, TestProperty[] expected) throws Exception {
        List propMetas = meta.getProperties();

        // the list here might be a partial list of the properties to validate, so only worry about
        // the ones in this list.

        // validate each expected argment against the actual metadata for this object.
        for (int i = 0; i < expected.length; i++) {
            // locate the target property
            BeanProperty propMeta = locateProperty(propMetas, expected[i]);
            assertNotNull("Component " + meta.getId() + " property " + expected[i].getName() + " not found", propMeta);
            try {
                // do the full validation now
                expected[i].validate(this, propMeta);
            } catch (Throwable e) {
                // just allowing this to go past will result in losing
                // about which component and property we're doing this on.  So
                // we'll throw a new assertion failure with the old one embedded.
                AssertionFailedError ee = new AssertionFailedError("Validation failure for component " + meta.getId() + " property=" + expected[i].getName() + ", error=" + e.getMessage());
                ee.initCause(e);
                throw ee;
            }
        }
    }


    /**
     * Locate the metadata for a specific named property.
     *
     * @param propMetas The collection of properties.
     * @param target    The property of interest.
     *
     * @return The located property metadata or null if not found.
     */
    public BeanProperty locateProperty(List propMetas, TestProperty target) {
        // the list here might be a partial list of the properties to validate, so only worry about
        // the ones in this list.
        Iterator it = propMetas.iterator();
        while (it.hasNext()) {
            BeanProperty meta = (BeanProperty)it.next();
            if (target.getName().equals(meta.getName())) {
                return meta;
            }
        }
        // not found
        return null;
    }


    /**
     * Get all of the component names for the wrappered module context.
     *
     * @return The Set of defined component names.
     */
    public Set getComponentIds() {
        return targetBlueprintContainer.getComponentIds();
    }

    /**
     * Retrieve any dependencies defined for a component using a
     * depends-on specification.
     *
     * @param componentId
     *               The target component id.
     *
     * @return The Set of explicit dependencies.  Returns an empty set if none
     *         are specified.
     * @exception Exception
     */
    public List getComponentDependencies(String componentId) throws Exception {
        // now validate the meta data is correct for the properties
        BeanMetadata meta = (BeanMetadata)targetBlueprintContainer.getComponentMetadata(componentId);
        return meta.getDependsOn();
    }


    /**
     * Search for an instance of a string within an array.
     *
     * @param target The target string.
     * @param list   The array to search.
     *
     * @return true if the string was found, false otherwise.
     */
    protected boolean contains(String target, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (target.equals(list[i])) {
                return true;
            }
        }
        return false;
    }


    /**
     * Perform any additional test phase cleanup actions.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void cleanup(BundleContext context) throws Exception {
        if (targetBlueprintContainer != null) {
            // this needs to release the acquired service.
            testContext.ungetService(contextRef);
            targetBlueprintContainer = null;
        }
        if (targetBundle != null) {
            System.out.println(">>>>>>>> Uninstalling bundle " + targetBundle);
            // uninstall the bundle unconditionally
            targetBundle.uninstall();
            targetBundle = null;
        }
    }

    /**
     * Validate a list of exported services that we expect to find
     *
     * @param expected The list of expected service definitions.
     *
     * @exception Exception
     */
    public void validateExportedServices(ExportedService[] expected) throws Exception {
        Collection exportedServices = targetBlueprintContainer.getMetadata(ServiceMetadata.class);

        assertEquals("Mismatch on the number of exported services", expected.length, exportedServices.size());
        for (int i = 0; i < expected.length; i++) {
            ExportedService e = (ExportedService)expected[i];
            ServiceMetadata service = locateServiceExport(exportedServices, e);
            assertNotNull("Exported service not found in metadata: " + e, service);
            // validate the metadata specifics
            e.validate(this, service);
        }
    }


    /**
     * Locate a matching metadata value for a service.
     *
     * @param services
     * @param service  The set of services for this bundle.
     *
     * @return The matching services metadata, or null if no match was found.
     */
    protected ServiceMetadata locateServiceExport(Collection services, ExportedService service) {
        Iterator i = services.iterator();
        while (i.hasNext()) {
            ServiceMetadata meta = (ServiceMetadata)i.next();
            if (service.matches(meta)) {
                return meta;
            }
        }
        return null;
    }


    /**
     * Validate a list of referenced services that we expect to find
     *
     * @param expected The list of expected service definitions.
     *
     * @exception Exception
     */
    public void validateReferencedServices(ReferencedService[] expected) throws Exception {
        Collection referencedServices = targetBlueprintContainer.getMetadata(ReferenceMetadata.class);

        assertEquals("Mismatch on the number of referenced services", expected.length, referencedServices.size());
        for (int i = 0; i < expected.length; i++) {
            ReferencedService e = (ReferencedService)expected[i];
            ServiceReferenceMetadata service = locateServiceReference(referencedServices, e);
            assertNotNull("Referenced service not found in metadata", service);
            // validate the metadata specifics
            e.validate(this, service);
        }
    }


    /**
     * Validate a list of defined components.  These must all be named metadata items
     *
     * @param expected The list of component definitions to validate
     *
     * @exception Exception
     */
    public void validateComponentMetadata(TestComponentMetadata[] expected) throws Exception {
        for (int i = 0; i < expected.length; i++) {
            String name = expected[i].getId();
            // wildcard request?
            if (name.equals("*")) {
                // we need to find a "likely" candidate.  For local components, this
                // generally matches on the class name, so this needs to be used with care
                expected[i].validate(this, getComponentMetadata(expected[i]));
            }
            else {
                // go validate the component
                expected[i].validate(this, getComponentMetadata(name));
            }
        }
    }


    /**
     * Locate a matching metadata value for a service.
     *
     * @param services
     * @param service  The set of services for this bundle.
     *
     * @return The matching services metadata, or null if no match was found.
     */
    protected ServiceReferenceMetadata locateServiceReference(Collection services, ReferencedService service) {
        Iterator i = services.iterator();
        while (i.hasNext()) {
            ServiceReferenceMetadata meta = (ServiceReferenceMetadata)i.next();
            if (service.matches(meta)) {
                return meta;
            }
        }
        return null;
    }
}


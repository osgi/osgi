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
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.context.ModuleContext;
import org.osgi.service.blueprint.context.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.LocalComponentMetadata;
import org.osgi.service.blueprint.reflect.MethodInjectionMetadata;
import org.osgi.service.blueprint.reflect.ParameterSpecification;
import org.osgi.service.blueprint.reflect.PropertyInjectionMetadata;
import org.osgi.service.blueprint.reflect.ReferenceValue;
import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.Value;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

/**
 * A wrapper around the published ModuleContext service for a managed
 * bundle.  This is used by the different validators, initializers,
 * and terminators to perform metadata-related operations.
 */
public class ModuleMetadata extends Assert implements TestValidator, TestCleanup {
    // the bundle context we're running in
    protected BundleContext testContext;
    // the bundle we're validating for
    protected Bundle targetBundle;
    // the resolved service reference to the target module context.
    protected ServiceReference contextRef;
    // the actual module context service
    protected ModuleContext targetModuleContext;


    /**
     * Create a metadata item for the given bundle.
     *
     * @param testContext    The text BundleContext (needed for services operations).
     * @param targetBundle   The target test bundle.
     */
    public ModuleMetadata(BundleContext testContext, Bundle targetBundle) {
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
        // just get the module context at this point
        getModuleContext();
    }

    /**
     * Retrieve the module context for the target bundle.
     */
    protected void getModuleContext() throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences("org.osgi.service.blueprint.context.ModuleContext",
            "(osgi.blueprint.context.symbolicname=" + targetBundle.getSymbolicName() + ")");
        assertNotNull("No ModuleContext located for bundle " + targetBundle.getSymbolicName(), refs);
        assertEquals("Bundle mismatch for ModuleContext instance.", targetBundle, refs[0].getBundle());

        // save the module context reference
        contextRef = refs[0];
        targetModuleContext = (ModuleContext)testContext.getService(contextRef);
    }


    /**
     * Retrieve a named component from module context.
     *
     * @param componentName
     * @param className
     */
    public Object getComponent(String componentName) throws Exception {
        // ensure we have a good context first
        getModuleContext();
        try {
            // get the object instance
            Object component = targetModuleContext.getComponent(componentName);
            assertNotNull("Component " + componentName + " not found in ModuleContext for " + targetBundle.getSymbolicName(), component);
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
        getModuleContext();
        try {
            Iterator componentNames = targetModuleContext.getComponentNames().iterator();
            while (componentNames.hasNext()) {
                String name = (String)componentNames.next();

                // get the metadata instance for the next name and check for a match
                ComponentMetadata component = targetModuleContext.getComponentMetadata(name);
                if (target.matches(component)) {
                    return component;
                }
            }
        } catch (NoSuchComponentException e) {
            fail("Component " + target.getName() + " not created for bundle " + targetBundle.getSymbolicName());
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
        getModuleContext();
        try {
            // get the object instance
            ComponentMetadata component = targetModuleContext.getComponentMetadata(componentName);
            assertNotNull("Component " + componentName + " not found in ModuleContext for " + targetBundle.getSymbolicName(), component);
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
        Dictionary properties = (Dictionary)getComponentProperty(componentId, ComponentTestInfo.COMPONENT_PROPERTIES);
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
        Dictionary properties = (Dictionary)getComponentProperty(componentId, ComponentTestInfo.COMPONENT_ARGUMENTS);
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
        LocalComponentMetadata meta = (LocalComponentMetadata)targetModuleContext.getComponentMetadata(componentId);
        assertEquals("Classname for component " + componentId, className, meta.getClassName());
        assertEquals("init-method for component " + componentId, initMethod, meta.getInitMethodName());
        assertEquals("destroy-method for component " + componentId, destroyMethod, meta.getDestroyMethodName());
    }

    /**
     * Validate the constructor parameters for a component against an expected set.
     *
     * @param componentId   The target component id.
     * @param expected      The set of expected items.
     *
     * @exception Exception
     */
    public void validateConstructorMetadata(String componentId, TestParameter[] expected) throws Exception {
        try {
            // now validate the meta data is correct for the parameters
            LocalComponentMetadata meta = (LocalComponentMetadata)targetModuleContext.getComponentMetadata(componentId);
            this.validateConstructorMetadata(meta, expected);
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
    public void validateConstructorMetadata(LocalComponentMetadata meta, TestParameter[] expected) throws Exception {
        // determine which type of parameter injection needs to be validated.
        MethodInjectionMetadata factoryMethodMeta = meta.getFactoryMethodMetadata();
        System.out.println("~~~~~~~~~~~~~~=>"+factoryMethodMeta);
        if (factoryMethodMeta != null) {
            System.out.println("~~~~~~~~~~~~~~!>"+factoryMethodMeta.getParameterSpecifications().size());
            validateParameters(factoryMethodMeta.getParameterSpecifications(), expected);
        }
        else {
            System.out.println("~~~~~~~~~~~~~~?>"+meta.getConstructorInjectionMetadata().getParameterSpecifications().size());
            validateParameters(meta.getConstructorInjectionMetadata().getParameterSpecifications(), expected);
        }
    }
    
    /**
     * Validate the parameters for a component against an expected set.
     *
     * @param parmSpecs The parameter specs
     * @param expected  The set of expected items.
     *
     * @exception Exception
     */
    public void validateParameters(List parmSpecs, TestParameter[] expected) throws Exception {
        assertEquals("Mismatch in constructor parameter size", expected.length, parmSpecs.size());
        // validate each expected argment against the actual metadata for this constructor.
        for (int i = 0; i < expected.length; i++) {
            try {
                // explicitly set the index so it will compare.  The spec says this will
                // be set whether it was implicit or explicit, so enforce the ordering.
                // TODO:  Bugzilla 1155.  The index values are not getting set and -1
                // is returned, counter to what the spec says
//              expected[i].setIndex(i);
                expected[i].validate(this, (ParameterSpecification)parmSpecs.get(i));
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
    public void validatePartialConstructorMetadata(String componentId, TestParameter[] expected) throws Exception {
        // now validate the meta data is correct for the parameters
        LocalComponentMetadata meta = (LocalComponentMetadata)targetModuleContext.getComponentMetadata(componentId);
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
    public void validatePartialConstructorMetadata(LocalComponentMetadata meta, TestParameter[] expected) throws Exception {
        // determine which type of parameter injection needs to be validated.
        MethodInjectionMetadata factoryMeta = meta.getFactoryMethodMetadata();
        if (factoryMeta != null) {
            validatePartialConstructorParameters(meta, factoryMeta.getParameterSpecifications(), expected);
        }
        else {
            validatePartialConstructorParameters(meta, meta.getConstructorInjectionMetadata().getParameterSpecifications(), expected);
        }
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
    public void validatePartialConstructorParameters(LocalComponentMetadata meta, List parms, TestParameter[] expected) throws Exception {
        assertEquals("Mismatch in constructor parameter size", expected.length, parms.size());
        // validate each expected argment against the actual metadata for this constructor.
        for (int i = 0; i < expected.length; i++) {
            try {
                // validate against the expected index position.
                expected[i].validate(this, (ParameterSpecification)parms.get(expected[i].getIndex()));
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
        LocalComponentMetadata meta = (LocalComponentMetadata)targetModuleContext.getComponentMetadata(componentId);
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
    public void validateFactoryMetadata(LocalComponentMetadata meta, String factoryMethodName, String staticFactoryClassName, TestValue factoryTestComponentValue) throws Exception {
        MethodInjectionMetadata methodMetadata = meta.getFactoryMethodMetadata();
        assertNotNull("Component " + meta.getName() + " factory metadata expected", methodMetadata);
        
        // validate factory method name
        assertEquals("Factory method for component " + meta.getName(), factoryMethodName, methodMetadata.getName());
        
        // validate class name
        assertEquals("Component " + meta.getName() + " class name mismatch", staticFactoryClassName, meta.getClassName());
        
        // validate for instance factory
        if (factoryTestComponentValue != null) { //optional validate..
            Value factoryComponentValue = meta.getFactoryComponent();
            assertNotNull("Component " + meta.getName() + "factory component definition expected", factoryComponentValue);
            // for the named factories, we expect this to be a RefernceValue.
            assertTrue("Factory ReferenceValue expected", factoryComponentValue instanceof ReferenceValue);
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
            validatePropertyMetadata((LocalComponentMetadata)targetModuleContext.getComponentMetadata(componentId), expected);
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
    public void validatePropertyMetadata(LocalComponentMetadata meta, TestProperty[] expected) throws Exception {
        Collection propMetas = meta.getPropertyInjectionMetadata();

        assertEquals("Mismatch in property set size", expected.length, propMetas.size());
        Iterator it = propMetas.iterator();
        // validate each expected argment against the actual metadata for this constructor.
        int i = 0;
        while (it.hasNext()) {
            try {
                expected[i++].validate(this, (PropertyInjectionMetadata)it.next());
            } catch (Throwable e) {
                // just allowing this to go past will result
                // about which component and property we're doing this on.  So
                // we'll throw a new assertion failure with the old one embedded.
                AssertionFailedError ee = new AssertionFailedError("Validation failure for component property=" + expected[i - 1].getName() + ", error=" + e.getMessage());
                ee.initCause(e);
                throw ee;
            }
        }
    }


    /**
     * Get all of the component names for the wrappered module context.
     *
     * @return The Set of defined component names.
     */
    public Set getComponentNames() {
        return targetModuleContext.getComponentNames();
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
    public Set getComponentDependencies(String componentId) throws Exception {
        // now validate the meta data is correct for the properties
        LocalComponentMetadata meta = (LocalComponentMetadata)targetModuleContext.getComponentMetadata(componentId);
        return meta.getExplicitDependencies();
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
        if (targetModuleContext != null) {
            // this needs to release the acquired service.
            testContext.ungetService(contextRef);
            targetModuleContext = null;
        }
        if (targetBundle != null) {
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
        Collection exportedServices = targetModuleContext.getExportedServicesMetadata();

        assertEquals("Mismatch on the number of exported services", exportedServices.size(), expected.length);
        for (int i = 0; i < expected.length; i++) {
            ExportedService e = (ExportedService)expected[i];
            ServiceExportComponentMetadata service = locateServiceExport(exportedServices, e);
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
    protected ServiceExportComponentMetadata locateServiceExport(Collection services, ExportedService service) {
        Iterator i = services.iterator();
        while (i.hasNext()) {
            ServiceExportComponentMetadata meta = (ServiceExportComponentMetadata)i.next();
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
        Collection referencedServices = targetModuleContext.getReferencedServicesMetadata();

        assertEquals("Mismatch on the number of referenced services", referencedServices.size(), expected.length);
        for (int i = 0; i < expected.length; i++) {
            ReferencedService e = (ReferencedService)expected[i];
            ServiceReferenceComponentMetadata service = locateServiceReference(referencedServices, e);
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
            String name = expected[i].getName();
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
    protected ServiceReferenceComponentMetadata locateServiceReference(Collection services, ReferencedService service) {
        Iterator i = services.iterator();
        while (i.hasNext()) {
            ServiceReferenceComponentMetadata meta = (ServiceReferenceComponentMetadata)i.next();
            if (service.matches(meta)) {
                return meta;
            }
        }
        return null;
    }
}


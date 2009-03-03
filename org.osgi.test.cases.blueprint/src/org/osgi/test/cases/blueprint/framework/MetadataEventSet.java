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

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * An event set for processing all standard events we expect to
 * see from a "normal" test case phase that uses bundle metadata
 * to validate the test results.  For the StandardTestController,
 * this would be the standard start phase.  For other tests,
 * this may extend to multiple phases.
 */
public class MetadataEventSet extends EventSet {
    // the special validator that validates the module context and also
    // give access to component metadata.
    protected ModuleMetadata moduleContext;

    public MetadataEventSet(ModuleMetadata moduleContext, BundleContext testBundle, Bundle bundle) {
        super(testBundle, bundle);
        this.moduleContext = moduleContext;
        moduleContext = new ModuleMetadata(testBundle, bundle);
    }

    /**
     * override for addValidator to ensure that MetadataValidators have the module context instance injected.
     *
     * @param v      The new validator to add.
     */
    public void addValidator(TestValidator v) {
        // inject the module context instance if this is required.
        if (v instanceof MetadataAware) {
            ((MetadataAware)v).setModuleMetadata(moduleContext);
        }
        // complete the add operation
        super.addValidator(v);
    }


    /**
     * Add an initializer to an event set.
     *
     * @param v      The new initializer to add.
     */
    public void addInitializer(TestInitializer v) {
        // inject the module context instance if this is required.
        if (v instanceof MetadataAware) {
            ((MetadataAware)v).setModuleMetadata(moduleContext);
        }
        // complete the add operation
        super.addInitializer(v);
    }


    /**
     * Add a terminator to an event set.
     *
     * @param v      The new terminator to add.
     */
    public void addTerminator(TestCleanup v) {
        // inject the module context instance if this is required.
        if (v instanceof MetadataAware) {
            ((MetadataAware)v).setModuleMetadata(moduleContext);
        }
        // complete the add operation
        super.addTerminator(v);
    }


    /**
     * Add an event to the expected list.
     *
     * @param event  The expected test event.
     */
    public void addEvent(TestEvent event) {
        // inject the module context instance if this is required.
        if (event instanceof MetadataAware) {
            ((MetadataAware)event).setModuleMetadata(moduleContext);
        }
        super.addEvent(event);
    }

    /**
     * Add an event to the failure list.
     *
     * @param event  An event that will cause a test failure if received.
     */
    public void addFailureEvent(TestEvent event) {
        // inject the module context instance if this is required.
        if (event instanceof MetadataAware) {
            ((MetadataAware)event).setModuleMetadata(moduleContext);
        }
        super.addFailureEvent(event);
    }

    /**
     * Add a validator to verify component state at the end of the phase.
     *
     * @param componentId
     *                  The target component id.
     * @param className The target class name.
     */
    public void validateComponent(String componentId, Class componentClass)
    {
        validateComponent(componentId, componentClass, null);
    }

    /**
     * Add a component validator that also validates the internal state
     * of the final component instance.  The target component must
     * implement the ComponentTestInfo interface.
     *
     * @param componentId
     *                  The component id.
     * @param className The component class name.
     * @param props     The set of properties to validate.
     */
    public void validateComponent(String componentId, Class componentClass, Dictionary props)
    {
        addValidator(new ComponentValidator(componentId, componentClass, props));
    }

    /**
     * Validate the value of a named property injected into a component.
     *
     * @param componentId
     *               The componentId owning the property.
     * @param name   The name of the property.
     * @param value  The expected value.
     */
    public void validateComponentProperty(String componentId, String name, Object value) {
        addValidator(new PropertyValueValidator(componentId, name, value));
    }

    /**
     * Validate the value of a named property injected into a component.
     *
     * @param componentId
     *               The componentId owning the property.
     * @param name   The name of the property.
     * @param value  The expected value.
     * @param clz    The expected property class.
     */
    public void validateComponentProperty(String componentId, String name, Object value, Class clz) {
        addValidator(new PropertyValueValidator(componentId, name, value, clz));
    }


    /**
     * Validate the value of a named argument injected into a component.
     *
     * @param componentId
     *               The componentId owning the argument.
     * @param name   The name of the argument.
     * @param value  The expected value.
     */
    public void validateComponentArgument(String componentId, String name, Object value) {
        addValidator(new ArgumentValueValidator(componentId, name, value));
    }


    /**
     * Validate the value of a named argument injected into a component.
     *
     * @param componentId
     *               The componentId owning the argument.
     * @param name   The name of the argument.
     * @param value  The expected value.
     * @param clz    The expected property class.
     */
    public void validateComponentArgument(String componentId, String name, Object value, Class clz) {
        addValidator(new ArgumentValueValidator(componentId, name, value, clz));
    }
}


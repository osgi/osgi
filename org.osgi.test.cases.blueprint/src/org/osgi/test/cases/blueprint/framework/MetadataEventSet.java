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


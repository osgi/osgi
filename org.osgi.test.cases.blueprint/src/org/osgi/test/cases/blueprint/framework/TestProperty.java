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

import org.osgi.service.blueprint.reflect.PropertyInjectionMetadata;

import junit.framework.Assert;

public class TestProperty extends Assert {
    // the target property name
    protected String propertyName;
    // expected property metadata type
    protected TestValue propertyTestValue;


    public TestProperty(TestValue propertyTestValue, String propertyName) {
        this.propertyTestValue = propertyTestValue;
        this.propertyName = propertyName;
    }


    public TestProperty(String value, String propertyName) {
        this.propertyTestValue = new TestStringValue(value);
        this.propertyName = propertyName;
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, PropertyInjectionMetadata spec) throws Exception {
        // validate the name
        assertEquals("Property name mismatch", propertyName, spec.getName());
        // and validate the property type information
        if (propertyTestValue != null) {
            propertyTestValue.validate(moduleMetadata, spec.getValue());
        }
    }

    public String getName() {
        return propertyName;
    }
}


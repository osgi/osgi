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

import org.osgi.service.blueprint.reflect.ParameterSpecification;

import junit.framework.Assert;

public class TestParameter extends Assert {
    // expected parameter metadata type
    protected TestValue parameterTestValue;
    // explicitly named parameter type (optional, if not specified)
    protected String typeName;
    // a potential explicit parameter index postion
    protected int index;

    public TestParameter(String value) {
        // the parameter type is inferred from specified position
        this(new TestStringValue(value), null, -1);
    }

    public TestParameter(String value, int index) {
        // the parameter type is inferred from specified position
        this(new TestStringValue(value), null, index);
    }

    public TestParameter(TestValue parameterType) {
        // the parameter type is inferred from specified position
        this(parameterType, null, -1);
    }

    public TestParameter(TestValue parameterType, Class type) {
        // the parameter type is explicitly specified
        this(parameterType, type, -1);
    }

    public TestParameter(TestValue parameterType, int index) {
        // the index has been specified
        this(parameterType, null, index);
    }

    /**
     * Fully qualified parameter
     *
     * @param parameterType
     *                 The expected Value type returned by getValue();
     * @param typeName The name of the type.  If specified, the ParameterSpecification
     *                 is expected to be a NamedParameterSpecification.
     * @param index    An explicit index position. If specified, the ParameterSpecification
     *                 is expected to be a NamedParameterSpecification.
     */
    public TestParameter(TestValue parameterType, Class type, int index) {
        this.parameterTestValue = parameterType;
        // we don't always have a type name here
        if (type != null) {
            this.typeName = type.getName();
        }
        this.index = index;
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, ParameterSpecification spec) throws Exception {
        // validate any type name explicitly specified on the XML.  This could be null also,
        // if not explicitly specified (common, actually)
        assertEquals("Type parameter type mismatch", typeName, spec.getTypeName());
        // expecting an index position?

        /** TODO:  Bugzilla 1155.  The RI is all over the charts with non-explicit index
         * positions.  So if we're using -1 for a check value, then don't even bother validating. */
        if (index != -1) {
            assertEquals("Indexed parameter position mismatch", index, spec.getIndex());
        }
        // validate the value type also
        parameterTestValue.validate(moduleMetadata, spec.getValue());
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}


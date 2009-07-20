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

import org.osgi.service.blueprint.reflect.BeanArgument;

import junit.framework.Assert;

public class TestArgument extends Assert {
    // expected parameter metadata type
    protected TestValue parameterTestValue;
    // explicitly named parameter type (optional, if not specified)
    protected String typeName;
    // a potential explicit parameter index postion
    protected int index;

    public TestArgument(String value) {
        // the parameter type is inferred from specified position
        this(new TestStringValue(value), (String) null, -1);
    }

    public TestArgument(String value, int index) {
        // the parameter type is inferred from specified position
        this(new TestStringValue(value), (String) null, index);
    }

    public TestArgument(TestValue parameterType) {
        // the parameter type is inferred from specified position
        this(parameterType, (String) null, -1);
    }

    public TestArgument(TestValue parameterType, Class type) {
        // the parameter type is explicitly specified
        this(parameterType, type, -1);
    }

    public TestArgument(TestValue parameterType, int index) {
        // the index has been specified
        this(parameterType, (String) null, index);
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
    public TestArgument(TestValue parameterType, Class type, int index) {
        this.parameterTestValue = parameterType;
        // we don't always have a type name here
        if (type != null) {
            this.typeName = type.getName();
        }
        this.index = index;
    }

    public TestArgument(TestValue parameterType, String typeName, int index) {
        this.parameterTestValue = parameterType;
        this.typeName = typeName;
        this.index = index;
    }

    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, BeanArgument spec) throws Exception {
        // validate any type name explicitly specified on the XML.  This could be null also,
        // if not explicitly specified (common, actually)
        assertEquals("Type parameter type mismatch", typeName, spec.getValueType());
        // this should be sorted out now. This is either an explicitly specified
        // position or -1.
        assertEquals("Indexed parameter position mismatch", index, spec.getIndex());
        // validate the value type also
        parameterTestValue.validate(blueprintMetadata, spec.getValue());
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}


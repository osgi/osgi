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

/**
 * Test the parameter specification for a named component reference.
 */
public class StringArgument extends TestArgument {

    /**
     * This form just validates we have a string parameter, and nothing more.
     */
    public StringArgument() {
        super(new TestStringValue());
    }

    /**
     * A "typeless" specification, just using a string
     * value.
     *
     * @param value  The source string value.
     */
    public StringArgument(String value) {
        super(new TestStringValue(value));
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> and no type is specified
     * on the value.
     *
     * @param targetType The target parameter type.
     * @param value      The source string value for the conversion.
     */
    public StringArgument(Class targetType, String value) {
        super(new TestStringValue(value), targetType);
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> and no type is specified
     * on the value, and we're also not validating the string value
     *
     * @param targetType The target parameter type.
     * @param value      The source string value for the conversion.
     */
    public StringArgument(Class targetType) {
        super(new TestStringValue(), targetType);
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> AND a type is specified
     * on the value.
     *
     * @param targetType The target parameter type. specified in <constructor-arg type="">
     * @param source     The source string value for the conversion.
     * @param valueType  The value type, specified in <value type="">
     */
    public StringArgument(Class targetType, String source, Class valueType) {
        super(new TestStringValue(valueType, source), targetType);
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> AND a type is specified
     * on the value.
     *
     * @param targetType The target parameter type.
     * @param valueType  The type specified on the value.
     */
    public StringArgument(Class targetType, Class valueType) {
        super(new TestStringValue(valueType), targetType);
    }
}


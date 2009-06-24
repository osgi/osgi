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

package org.osgi.test.cases.blueprint.components.injection;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Small test class for testing constructor cardinality
 * errors on bean components.  This class can be used
 * directly, as a static factory and also as an instance
 * factory.
 */
public class ConstructorCardinality extends BaseTestComponent {
    /**
     * Default constructor for using this as an instance factory.
     */
    public ConstructorCardinality() {
        super("constructorCardinality");
    }


    /**
     * Simple injection with two string arguments.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public ConstructorCardinality(String arg1, String arg2) {
        super("twoStringArgs");
        setArgumentValue("arg1", arg1);
        setArgumentValue("arg2", arg2, String.class);
    }


    /**
     * Static factory method for creating an instance.
     *
     * @param arg1   The first argument
     * @param arg2   The second argument
     *
     * @return A ConstructorCardinality instance.
     */
    static public ConstructorCardinality makeStatic(String arg1, String arg2) {
        return new ConstructorCardinality(arg1, arg2);
    }


    /**
     * instance factory method for creating an instance.
     *
     * @param arg1   The first argument
     * @param arg2   The second argument
     *
     * @return A ConstructorCardinality instance.
     */
    public ConstructorCardinality makeInstance(String arg1, String arg2) {
        return new ConstructorCardinality(arg1, arg2);
    }
}

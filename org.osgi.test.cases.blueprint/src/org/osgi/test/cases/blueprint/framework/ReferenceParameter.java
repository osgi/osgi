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
public class ReferenceParameter extends TestParameter {
    public ReferenceParameter(String componentId) {
        // the parameter type is inferred from specified position
        this(componentId, -1);
    }


    public ReferenceParameter(String componentId, Class  type) {
        // the parameter type is inferred from specified position
        super(new TestReferenceValue(componentId), type);
    }

    public ReferenceParameter(String componentId, int index) {
        // the index has been specified
        super(new TestReferenceValue(componentId), index);
    }
}


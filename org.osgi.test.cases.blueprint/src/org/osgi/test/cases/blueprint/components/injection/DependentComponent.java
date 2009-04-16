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

/**
 * A class to act as a target for dependent component checks.
 */
public class DependentComponent extends ComponentInjection {
    /**
     * Simple injection with just a component id.  This is used for tests that
     * only test property injection.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public DependentComponent(String componentId) {
        super(componentId);
        // add this to the initialized dependent component list
        addDependentComponent(componentId, this);
    }
}

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

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Test component used to validate singleton/prototype component
 * semantics.  This component will be injected with a Set created
 * using component references.  This Set will be constructed using
 * references to either singleton or prototype components.  At
 * injection time, the set will be validated against an expected
 * number of components in the set.
 */
public class ScopeComponent extends BaseTestComponent {
    // the number of elements we expect in the Set
    protected int expectedCount;

    /**
     * Create a component that is expecting the indicated number of
     * elements in an injected set property.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     * @param expectedCount
     *               The size of the expected set.
     */
    public ScopeComponent(String componentId, int expectedCount) {
        super(componentId);
        this.expectedCount = expectedCount;
    }


    /**
     * References property used to validate singleton/prototype
     * reference sets.
     *
     * @param refs   The injected Set of references.
     */
    public void setReferences(Set refs) {
        AssertionService.assertEquals(this, "Unexpected component Set size", expectedCount, refs.size());
    }


    /**
     * References property used to validate singleton/prototype
     * reference sets.
     *
     * @param refs   The injected Set of references.
     */
    public void setListReferences(List refs) {
        AssertionService.assertEquals(this, "Unexpected component List size", expectedCount, refs.size());
        Set set = new HashSet(refs);
        AssertionService.assertEquals(this, "Unexpected component converted Set size", expectedCount, refs.size());
    }
}


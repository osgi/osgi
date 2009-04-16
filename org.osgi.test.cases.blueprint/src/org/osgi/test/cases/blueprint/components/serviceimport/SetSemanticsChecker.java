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

package org.osgi.test.cases.blueprint.components.serviceimport;

import org.osgi.test.cases.blueprint.services.AssertionService;


/**
 * Test that ref-sets actually function like sets.  Will inject a set and and list
 * derived from the same interface request.  These will have different numbers of
 * elements because all of the services will compare equal.
 */
public class SetSemanticsChecker extends ReferenceCollectionChecker {
    public SetSemanticsChecker(String componentId) {
        super(componentId);
    }

    /**
     * This is the actual test method.  After the component is
     * instantiated, the init method is called to drive the
     * component test logic.  Each concrete test will override this
     * and verify that the injected results are correct.  Tests
     * dealing with service dynamics will use the ServiceManager to
     * modify the state of the registered services.
     */
    public void init() {
        // we're just going to test the injected values here.  Other tests so much more thorough
        // validation.
        AssertionService.assertEquals(this, "Mismatch on ref-set size", 1, injectedSet.size());
        AssertionService.assertEquals(this, "Mismatch on ref-list size", 3, injectedList.size());
        super.init();
    }
}


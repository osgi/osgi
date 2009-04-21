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

package org.osgi.test.cases.blueprint.components.errors;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Simple test component that throws an exception when
 * the destroy method is called.  Used to test errors
 * during BlueprintContext creation.
 */
public class DestroyMethodException extends BaseTestComponent {
    public DestroyMethodException(String componentId) {
        // we do this because we want to verify the constructor is getting called.
        super(componentId);
    }


    /**
     * A destroy method that will throw an exception.
     */
    public void destroy() {
        super.destroy();
        throw new NullPointerException("Intentional exception from component " + componentId);
    }


    /**
     * A destroy method with a bad signature
     */
    public int badDestroy(int a) {
        AssertionService.fail(this, "Invalid destroy method call");
        return 0;
    }
}


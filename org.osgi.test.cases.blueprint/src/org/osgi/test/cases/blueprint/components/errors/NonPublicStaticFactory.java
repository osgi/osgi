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

import org.osgi.test.cases.blueprint.components.comp1.AltSimpleTestComponent;
import org.osgi.test.cases.blueprint.components.comp1.SimpleTestComponent;

class NonPublicStaticFactory {
    // make this non-instantiable
    private NonPublicStaticFactory() {}

    public static Object createSimple() {
        return new SimpleTestComponent("static-comp1");
    }

    public static Object createSimple(String id) {
        return new SimpleTestComponent(id);
    }

    public static AltSimpleTestComponent createAlt(String id) {
        return new AltSimpleTestComponent(id);
    }

    /**
     * Method to test attempting to invoke non-static factory method.
     *
     * @param id     The component id.
     *
     * @return A new instance (but never should be invoked).
     */
    public Object nonStaticMethod(String id) {
        return new SimpleTestComponent(id);
    }
}



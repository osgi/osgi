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
 * Small test class for testing constructor type compatibility
 * rules for constructor disambiguation.  This will have
 * constructor methods, static factory methods, and component
 * factory methods with an assortment of signatures to
 * test the disambiguation rules.  Many of these rules will
 * require type conversion in order to derive the correct signature,
 */
public class TypeCompatibility extends BaseTestComponent {
    /**
     * Default constructor for using this as an instance factory.
     */
    public TypeCompatibility() {
        super("typeCompatibility");
    }
}

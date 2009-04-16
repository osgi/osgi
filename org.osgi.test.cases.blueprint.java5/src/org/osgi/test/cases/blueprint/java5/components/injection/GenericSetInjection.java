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

package org.osgi.test.cases.blueprint.java5.components.injection;

import java.net.URL;
import java.util.Set;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class GenericSetInjection extends BaseTestComponent {

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public GenericSetInjection() {
        super("GenericSetInjection");
    }

    /**
     * Simple injection with a converted array argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public GenericSetInjection(Set<Double> arg1) {
        super("GenericSetInjection");
        setArgumentValue("arg1", arg1, Set.class);
    }

    public void setInteger(Set<Integer> arg1) {
        setPropertyValue("integer", arg1, Set.class);
    }

    public void setUrl(Set<URL> arg1) {
        setPropertyValue("url", arg1, Set.class);
    }
}


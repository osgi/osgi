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
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class GenericMapInjection extends BaseTestComponent {

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public GenericMapInjection() {
        super("GenericMapInjection");
    }

    /**
     * Simple injection with a converted array argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public GenericMapInjection(Map<Long, Boolean> arg1) {
        super("GenericMapInjection");
        setArgumentValue("arg1", arg1, Map.class);
    }

    public void setMap1(Map<Double, String> arg1) {
        setPropertyValue("map1", arg1, Map.class);
    }

    public void setMap2(Map<String, URL> arg1) {
        setPropertyValue("map2", arg1, Map.class);
    }

    public void setConcurrentMap(ConcurrentMap arg1) {
        setPropertyValue("concurrentMap", arg1, ConcurrentMap.class);
    }
}


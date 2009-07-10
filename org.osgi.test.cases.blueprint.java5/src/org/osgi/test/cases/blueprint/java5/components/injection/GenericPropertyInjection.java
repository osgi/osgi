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

/**
 * Simple target class for testing generic injections.
 */
public class GenericPropertyInjection extends BaseTestComponent {

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public GenericPropertyInjection(String componentId) {
        super(componentId);
    }

    public void setSuit(Suit value) {
        setPropertyValue("suit", value, Suit.class);
    }

    public void setStringHolder(GenericHolder<String> value) {
        setPropertyValue("stringHolder", value, GenericHolder.class);
    }

    public void setBooleanHolder(GenericHolder<Boolean> value) {
        setPropertyValue("boolHolder", value, GenericHolder.class);
    }
}


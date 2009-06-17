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

package org.osgi.test.cases.blueprint.components.staticfactory;

import org.osgi.test.cases.blueprint.services.AssertionService;

public class TypeStaticFactory {
    // make this non-instantiable
    private TypeStaticFactory() {}

    static public Integer createInteger(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return new Integer(v);
    }

    static public Long createLong(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return new Long(v);
    }

    static public Double createDouble(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return new Double(v);
    }

    static public Float createFloat(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return new Float(v);
    }

    static public Short createShort(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return new Short(v);
    }
}


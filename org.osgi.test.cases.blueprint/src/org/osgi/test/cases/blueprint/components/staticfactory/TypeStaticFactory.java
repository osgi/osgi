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
        return Integer.valueOf(v);
    }

    static public Long createLong(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return Long.valueOf(v);
    }

    static public Double createDouble(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return Double.valueOf(v);
    }

    static public Float createFloat(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return Float.valueOf(v);
    }

    static public Short createShort(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
        return Short.valueOf(v);
    }

    static public int createPrimInt(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
		return Integer.parseInt(v);
    }

    static public long createPrimLong(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
		return Long.parseLong(v);
    }

    static public double createPrimDouble(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
		return Double.parseDouble(v);
    }

    static public float createPrimFloat(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
		return Float.parseFloat(v);
    }

    static public short createPrimShort(String v) {
        // broadcast a factory method event
        AssertionService.sendEvent(AssertionService.FACTORY_CALLED);
		return Short.parseShort(v);
    }
}


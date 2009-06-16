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

import org.osgi.service.blueprint.container.Converter;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class EuropeanRegionCodeConverter extends BaseTestComponent implements Converter {

    public EuropeanRegionCodeConverter(String compId) {
        super(compId);
    }
    public Object convert(Object source, Class toType) throws Exception {
        AssertionService.sendEvent(this, AssertionService.METHOD_CALLED);
        if (source instanceof String && (RegionCode.class.isAssignableFrom(toType) && toType.isAssignableFrom(AsianRegionCode.class))) {
            return new EuropeanRegionCode((String)source);
        }
        // we're supposed to throw an exception if we can't convert
        throw new Exception("Unconvertable object type");
    }

    public boolean canConvert(Object value, Class toType) {
        return (RegionCode.class.isAssignableFrom(toType) && toType.isAssignableFrom(EuropeanRegionCode.class)) && value instanceof String;
    }
}

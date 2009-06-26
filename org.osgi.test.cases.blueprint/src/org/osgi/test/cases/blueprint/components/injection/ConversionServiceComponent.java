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
import org.osgi.service.blueprint.container.ReifiedType;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class ConversionServiceComponent extends BaseTestComponent {
    // our conversion service, injected into the constructor
    protected Converter blueprintConverter;

    public ConversionServiceComponent(String componentId, Converter blueprintConverter) {
        super(componentId);
        this.blueprintConverter = blueprintConverter;
    }

    /**
     * This uses the conversion service to convert the
     * input value into a Boolean value that is assigned
     * to the property.  This will be used with registered
     * and unregistered converters.
     *
     * @param value  The input value.
     */
    public void setConversion(String value) throws Exception {
        // convert this to a boolean for assignment.
        setPropertyValue("conversion", blueprintConverter.convert(value, new ReifiedType(Boolean.class)), Boolean.class);
    }
}


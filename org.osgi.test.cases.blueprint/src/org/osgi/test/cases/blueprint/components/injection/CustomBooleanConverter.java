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

public class CustomBooleanConverter implements Converter {

    public Object convert(Object source, ReifiedType toType) throws Exception {
        if (source instanceof String && toType.getRawClass() == Boolean.class) {
            String strValue = (String)source;
            if (strValue.equals("T")) {
                return new Boolean(true);
            }
            else if (strValue.equals("F")) {
                return new Boolean(false);
            }
            //this make the module context only support converting T/F to Boolean?
        }

        // we're supposed to throw an exception if we can't convert
        throw new Exception("Unconvertable object type");
    }

    public boolean canConvert(Object value, ReifiedType toType) {
        return toType.getRawClass() == Boolean.class && value instanceof String;
    }
}

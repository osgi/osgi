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

import java.util.Collection;
import java.util.LinkedList;

import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.CollapsedType;

public class LinkedListConverter implements Converter {
    public Object convert(Object source, CollapsedType toType) throws Exception {
        if (source instanceof Collection && toType.getRawClass() == LinkedList.class) {
            return new LinkedList((Collection)source);
        }
        // we're supposed to throw an exception if we can't convert
        throw new Exception("Unconvertable object type");
    }

    public boolean canConvert(Object value, CollapsedType toType) {
        return toType.getRawClass() == LinkedList.class && value instanceof Collection;
    }
}


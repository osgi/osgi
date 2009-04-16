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

package org.osgi.test.cases.blueprint.namespace;

import org.osgi.service.blueprint.reflect.TypedStringValue;


/**
 * A TypedStringValue implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class TypedStringValueImpl implements TypedStringValue {
    private String stringValue;
    private String typeName;

    public TypedStringValueImpl(String value) {
        this.stringValue = value;
        this.typeName = null;
    }

    public TypedStringValueImpl(String value, String name) {
        this.stringValue = value;
        this.typeName = name;
    }

    public TypedStringValueImpl(TypedStringValue source) {
        this.stringValue = source.getStringValue();
        this.typeName = source.getTypeName();
    }

    /**
     * The string value (unconverted) of this value).
     */
    public String getStringValue() {
        return stringValue;
    }
    /**
     * The name of the type to which this value should be coerced. May be null.
     */
    public String getTypeName() {
        return typeName;
    }
}


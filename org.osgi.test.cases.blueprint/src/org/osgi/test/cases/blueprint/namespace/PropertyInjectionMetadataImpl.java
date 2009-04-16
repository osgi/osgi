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

import org.osgi.service.blueprint.reflect.PropertyInjectionMetadata;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A PropertyInjectionMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class PropertyInjectionMetadataImpl implements PropertyInjectionMetadata {
    private String name;
    private Value value;

    public PropertyInjectionMetadataImpl() {
    }

    public PropertyInjectionMetadataImpl(String name, Value value) {
        this.name = name;
        this.value = value;
    }

    public PropertyInjectionMetadataImpl(PropertyInjectionMetadata source) {
        name = source.getName();
        value = NamespaceUtil.cloneValue(source.getValue());
    }

    /**
     * The name of the property to be injected, following JavaBeans conventions.
     *
     * @return the property name.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The value to inject the property with.
     *
     * @return the property value.
     */
    public Value getValue() {
        return value;
    }


    public void setValue(Value value) {
        this.value = value;
    }
}


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

import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Metadata;

/**
 * Metadata used in a Bean Component to inject arguments in a method or
 * constructor. This metadata class describes the <code>argument</element>
 */
public class BeanPropertyImpl implements BeanProperty {
    private String name;
    private Metadata value;

    public BeanPropertyImpl() {
    }

    public BeanPropertyImpl(String name, Metadata value) {
        this.name = name;
        this.value = value;
    }

    public BeanPropertyImpl(BeanProperty source) {
        name = source.getName();
        value = NamespaceUtil.cloneMetadata(source.getValue());
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
    public Metadata getValue() {
        return value;
    }


    public void setValue(Metadata value) {
        this.value = value;
    }
}



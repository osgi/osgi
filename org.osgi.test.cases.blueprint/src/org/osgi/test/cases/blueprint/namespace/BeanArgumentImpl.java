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

import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.Metadata;



/**
 * Metadata used in a Bean Component to inject arguments in a method or
 * constructor. This metadata class describes the <code>argument</element>
 */
public class BeanArgumentImpl implements BeanArgument {
    private Metadata value;
    private String typeName;
    private int index;

    public BeanArgumentImpl() {
        // default index marker
        index = -1;
    }

    public BeanArgumentImpl(BeanArgument source) {
        value = NamespaceUtil.cloneMetadata(source.getValue());
        typeName = source.getValueType();
        index = source.getIndex();
    }


    /**
     * The value to inject into the parameter.
     *
     * @return the parameter value
     */
    public Metadata getValue() {
        return value;
    }

    public void setValue(Metadata value) {
        this.value = value;
    }


    /**
     * The type to convert the value into when invoking the constructor or
     * factory method. If no explicit type was specified on the component
     * definition then this method returns null.
     *
     * @return the explicitly specified type to convert the value into, or
     * null if no type was specified in the component definition.
     */
    public String getValueType() {
        return typeName;
    }

    public void setValueType(String typeName) {
        this.typeName = typeName;
    }


    /**
     * The (zero-based) index into the parameter list of the method or
     * constructor to be invoked for this parameter. This is determined
     * either by explicitly specifying the index attribute in the component
     * declaration, or by declaration order of constructor-arg elements if the
     * index was not explicitly set.
     *
     * @return the zero-based parameter index
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}


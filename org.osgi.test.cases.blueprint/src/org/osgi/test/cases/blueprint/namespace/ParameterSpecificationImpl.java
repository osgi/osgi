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

import org.osgi.service.blueprint.reflect.ParameterSpecification;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A ParameterSpecification implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class ParameterSpecificationImpl implements ParameterSpecification {
    private Value value;
    private String typeName;
    private int index;

    public ParameterSpecificationImpl() {
    }

    public ParameterSpecificationImpl(ParameterSpecification source) {
        value = NamespaceUtil.cloneValue(source.getValue());
        typeName = source.getTypeName();
        index = source.getIndex();
    }


    /**
     * The value to inject into the parameter.
     *
     * @return the parameter value
     */
    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
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
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
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


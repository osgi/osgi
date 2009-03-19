/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.namespace;

import org.osgi.service.blueprint.reflect.ParameterSpecification;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A ComponentMetadata implementation class used for
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


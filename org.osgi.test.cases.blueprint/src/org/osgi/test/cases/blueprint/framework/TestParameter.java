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

package org.osgi.test.cases.blueprint.framework;

import org.osgi.service.blueprint.reflect.ParameterSpecification;

import junit.framework.Assert;

public class TestParameter extends Assert {
    // expected parameter metadata type
    protected TestValue parameterType;
    // explicitly named parameter type (optional, if not specified)
    protected String typeName;
    // a potential explicit parameter index postion
    protected int index;

    public TestParameter(String value) {
        // the parameter type is inferred from specified position
        this(new TestStringValue(value), null, -1);
    }

    public TestParameter(String value, int index) {
        // the parameter type is inferred from specified position
        this(new TestStringValue(value), null, index);
    }

    public TestParameter(TestValue parameterType) {
        // the parameter type is inferred from specified position
        this(parameterType, null, -1);
    }

    public TestParameter(TestValue parameterType, Class type) {
        // the parameter type is explicitly specified
        this(parameterType, type, -1);
    }

    public TestParameter(TestValue parameterType, int index) {
        // the index has been specified
        this(parameterType, null, index);
    }

    /**
     * Fully qualified parameter
     *
     * @param parameterType
     *                 The expected Value type returned by getValue();
     * @param typeName The name of the type.  If specified, the ParameterSpecification
     *                 is expected to be a NamedParameterSpecification.
     * @param index    An explicit index position. If specified, the ParameterSpecification
     *                 is expected to be a NamedParameterSpecification.
     */
    public TestParameter(TestValue parameterType, Class type, int index) {
        this.parameterType = parameterType;
        // we don't always have a type name here
        if (type != null) {
            this.typeName = type.getName();
        }
        this.index = index;
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, ParameterSpecification spec) throws Exception {
        // validate any type name explicitly specified on the XML.  This could be null also,
        // if not explicitly specified (common, actually)
        assertEquals("Type parameter type mismatch", typeName, spec.getTypeName());
        // expecting an index position?

        /** TODO:  Bugzilla 1155.  The RI is all over the charts with non-explicit index
         * positions.  So if we're using -1 for a check value, then don't even bother validating. */
        if (index != -1) {
            assertEquals("Indexed parameter position mismatch", index, spec.getIndex());
        }
        // validate the value type also
        parameterType.validate(moduleMetadata, spec.getValue());
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}


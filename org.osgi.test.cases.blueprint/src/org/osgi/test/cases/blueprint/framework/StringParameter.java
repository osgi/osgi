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

/**
 * Test the parameter specification for a named component reference.
 */
public class StringParameter extends TestParameter {

    /**
     * This form just validates we have a string parameter, and nothing more.
     */
    public StringParameter() {
        super(new TestStringValue());
    }

    /**
     * A "typeless" specification, just using a string
     * value.
     *
     * @param value  The source string value.
     */
    public StringParameter(String value) {
        super(new TestStringValue(value));
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> and no type is specified
     * on the value.
     *
     * @param targetType The target parameter type.
     * @param value      The source string value for the conversion.
     */
    public StringParameter(Class targetType, String value) {
        super(new TestStringValue(value), targetType);
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> and no type is specified
     * on the value, and we're also not validating the string value
     *
     * @param targetType The target parameter type.
     * @param value      The source string value for the conversion.
     */
    public StringParameter(Class targetType) {
        super(new TestStringValue(), targetType);
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> AND a type is specified
     * on the value.
     *
     * @param targetType The target parameter type.
     * @param value      The source string value for the conversion.
     */
    public StringParameter(Class targetType, String value, Class valueType) {
        super(new TestStringValue(valueType, value), targetType);
    }


    /**
     * Create a String parameter where the type is specified
     * on the <constructor-arg> AND a type is specified
     * on the value.
     *
     * @param targetType The target parameter type.
     * @param valueType  The type specified on the value.
     */
    public StringParameter(Class targetType, Class valueType) {
        super(new TestStringValue(valueType), targetType);
    }
}


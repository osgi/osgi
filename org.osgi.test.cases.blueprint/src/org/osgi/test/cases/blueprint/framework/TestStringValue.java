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

import org.osgi.service.blueprint.reflect.TypedStringValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestStringValue extends TestValue {
    // The target conversion type (can be null)
    protected String targetTypeName;
    // the config file string value (optional validation)
    protected String value;


    /**
     * This version is when you have a string argument with
     * no explicit type specified, and you don't wish
     * to validate the value.
     */
    TestStringValue() {
        this(null, null);
    }

    public TestStringValue(Class targetType) {
        this(targetType, null);
    }

    public TestStringValue(String value) {
        this(null, value);
    }

    public TestStringValue(Class targetType, String value) {
        super(TypedStringValue.class);
        if (targetType != null) {
            this.targetTypeName = targetType.getName();
        }
        this.value = value;
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, Value v) throws Exception {
        super.validate(moduleMetadata, v);
        assertEquals("String conversion type mismatch", targetTypeName, ((TypedStringValue)v).getTypeName());
        // if we have a value to test on.  sometimes, the value is two complex
        // to specify directly.
        if (value != null) {
            assertEquals("String conversion value mismatch", value, ((TypedStringValue)v).getStringValue());
        }
    }

    /**
     * do a comparison between a real metadata item and our test validator.
     * This is used primarily to locate specific values in the different
     * CollectionValues.
     *
     * @param v      The target value item.
     *
     * @return True if this can be considered a match, false for any mismatch.
     */
    public boolean equals(Value v) {
        // must be of matching type
        if (!super.equals(v)) {
            return false;
        }
        // if no type was explicitly specified,
        if (targetTypeName == null && ((TypedStringValue)v).getTypeName() != null) {
            return false;
        }
        // must match on the type name
        if (!targetTypeName.equals(((TypedStringValue)v).getTypeName())) {
            return false;
        }

        if (value != null) {
            return value.equals(((TypedStringValue)v).getStringValue());
        }

        return true;
    }

    /**
     * Helpful override for toString().
     *
     * @return a descriptive string value for the type.
     */
    public String toString() {
        if (value != null) {
            return "String value " + value + " converted to " + targetTypeName;
        }
        else {
            return "String value converted to " + targetTypeName;
        }
    }
}


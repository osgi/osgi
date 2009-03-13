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

import org.osgi.service.blueprint.reflect.PropertyInjectionMetadata;

import junit.framework.Assert;

public class TestProperty extends Assert {
    // the target property name
    protected String propertyName;
    // expected property metadata type
    protected TestValue propertyTestValue;


    public TestProperty(TestValue propertyTestValue, String propertyName) {
        this.propertyTestValue = propertyTestValue;
        this.propertyName = propertyName;
    }


    public TestProperty(String value, String propertyName) {
        this.propertyTestValue = new TestStringValue(value);
        this.propertyName = propertyName;
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, PropertyInjectionMetadata spec) throws Exception {
        // validate the name
        assertEquals("Property name mismatch", propertyName, spec.getName());
        // and validate the property type information
        if (propertyTestValue != null) {
            propertyTestValue.validate(moduleMetadata, spec.getValue());
        }
    }

    public String getName() {
        return propertyName;
    }
}


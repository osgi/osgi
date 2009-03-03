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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.StringValueDescriptor;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

public class CompoundPropertyValidator extends MetadataValidator {

    private String componentId;
    private String compoundName;
    private ValueDescriptor expectedValue;

    public CompoundPropertyValidator(String componentId, String compoundName, String lastName, Object value, Class clz){
        this.componentId = componentId;
        this.compoundName = compoundName;
        this.expectedValue = new StringValueDescriptor(lastName, value, clz);
    }

    public void validate(BundleContext testContext) throws Exception {
        // ensure we have everything initialized
        super.validate(testContext);
        // the value must be equal

        Object componentObject = moduleMetadata.getComponent(componentId);
        if (componentObject instanceof ComponentTestInfo){
            ComponentTestInfo component = (ComponentTestInfo)componentObject;
            List names = split(compoundName);
            int i=0;
            for(i=0; i<names.size()-1; i++){
                component = (ComponentTestInfo)(((StringValueDescriptor)component.getProperty((String)names.get(i))).getValue());
            }
            StringValueDescriptor testValue = (StringValueDescriptor)(component.getProperty((String)names.get(i)));
            assertEquals("Compound Property " + compoundName + " value in component " + componentId, expectedValue, testValue);
        }else{
            fail("Component is not ComponentTestInfo.");
        }


    }

    /**
     * Simple replacement for the String split() method, which is
     * not available in the eeminimum profile.
     *
     * @param source The source string.
     *
     * @return A list of the tokenized tokens.
     */
    private List split(String source) {
        StringTokenizer tok = new StringTokenizer(source, ".");
        List result = new ArrayList();

        while (tok.hasMoreTokens()) {
            result.add(tok.nextToken());
        }

        return result;
    }

}

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

package org.osgi.test.cases.blueprint.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
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

        Object componentObject = blueprintMetadata.getComponent(componentId);
        if (componentObject instanceof BaseTestComponent){
            BaseTestComponent component = (BaseTestComponent)componentObject;
            List names = split(compoundName);
            int i=0;
            for(i=0; i < names.size() - 1; i++) {
                String name = (String)names.get(i);
                StringValueDescriptor value = (StringValueDescriptor)component.getPropertyValue(name);
                assertNotNull("Null subcomponent value in component " + componentId + " found for sub-name " + name, value);
                component = (BaseTestComponent)value.getValue();
                assertNotNull("Null subcomponent in component " + componentId + " found for sub-name " + name, component);
            }
            StringValueDescriptor testValue = (StringValueDescriptor)(component.getPropertyValue((String)names.get(i)));
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

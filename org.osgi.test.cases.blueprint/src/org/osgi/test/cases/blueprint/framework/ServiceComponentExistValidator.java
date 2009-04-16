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
import java.util.Dictionary;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

public class ServiceComponentExistValidator extends MetadataValidator {

    // the property comparison value
    protected ArrayList /*<ValueDescriptor>*/ expectedValueList = new ArrayList();

    // serviceInterfaceName must be the subclass of ComponentTestInfo
    private String serviceInterfaceName;



    public ServiceComponentExistValidator(String serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }

    /**
     * Add an expected property value.
     *
     * @param propertyName
     *               The name of the property value.
     * @param value  The expected property value.
     * @param clz    The expected class of the value.
     */
    public void addValue(String propertyName, Object expectedValue, Class clz){
        expectedValueList.add(new ValueDescriptor(propertyName, expectedValue, clz));
    }

    public void validate(BundleContext testContext) throws Exception {
        // ensure we have everything initialized
        super.validate(testContext);

        ServiceReference[] refs = testContext.getServiceReferences(serviceInterfaceName, null);
        assertNotNull("No target service found for:" + this.serviceInterfaceName, refs);

        // the value group must belong to one of the components
        for (int i=0; i<refs.length; i++){
            ComponentTestInfo comp = (ComponentTestInfo)testContext.getService(refs[i]);

            // get the property"s" of the component
            Dictionary componentProps = comp.getComponentProperties();
            Dictionary properties = (Dictionary) componentProps.get(ComponentTestInfo.COMPONENT_PROPERTIES); //the property"s"

            // try to find all expected value in the component
            if (properties!=null) {
                Iterator valueIterator = this.expectedValueList.iterator();
                // suppose all expected will be found
                boolean allExpectedValueFound = true;
                while (valueIterator.hasNext()){
                    ValueDescriptor expectedValue = (ValueDescriptor)valueIterator.next();
                    ValueDescriptor actualValue = (ValueDescriptor)properties.get(expectedValue.getName());
                    if (!expectedValue.equals(actualValue)) {
                        allExpectedValueFound = false;
                        break;
                    }
                }

                if (allExpectedValueFound){
                    // all expected are really found.
                    return;
                }
            }
        }

        fail("No component contains this property group");


    }


}

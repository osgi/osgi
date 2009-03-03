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

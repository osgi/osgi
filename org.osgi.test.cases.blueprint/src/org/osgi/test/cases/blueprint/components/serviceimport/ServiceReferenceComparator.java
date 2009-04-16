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

package org.osgi.test.cases.blueprint.components.serviceimport;

import java.util.Comparator;

import org.osgi.framework.ServiceReference;

/**
 * Test for injection of an empty List of services no services are available.  This
 * will also register a service using the service manager to see that the new
 * registration is picked up.
 */
public class ServiceReferenceComparator implements Comparator {

    public ServiceReferenceComparator() {
    }

    public int compare(Object obj1, Object obj2) {
        ServiceReference ref1 = (ServiceReference)obj1;
        ServiceReference ref2 = (ServiceReference)obj2;

        String comp1 = (String)ref1.getProperty("test.service.name");
        String comp2 = (String)ref2.getProperty("test.service.name");

        // this sorts inverted by component name
        return -comp1.compareTo(comp2);
    }
}


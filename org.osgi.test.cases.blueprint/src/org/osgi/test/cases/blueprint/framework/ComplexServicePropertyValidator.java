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

import static junit.framework.TestCase.*;

import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.TestServiceOne;

/**
 * A one-off test validator to check the values of
 * a very complex service property set used for
 * export.
 */
public class ComplexServicePropertyValidator
		implements TestValidator, BundleAware {
    // the bundle the service should be part of
    protected Bundle bundle;
    // the name of the interface to validate
    protected String interfaceName;


    /**
     * Create a registration validator.
     *
     * @param interfaceNames
     *               The set of interface names we expect to find this under.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     * @param filter A lookup filter for the service.
     * @param serviceProperties
     */
    public ComplexServicePropertyValidator() {
        this.bundle = null;   // we'll resolve this later
        // using a static name
        this.interfaceName = TestServiceOne.class.getName();
    }

    /**
     * Inject the event set's bundler into this validator instance.
     *
     * @param bundle The bundle associated with the hosting event set.
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Perform any additional validation checks at the end of a test phase.
     * This can perform any validation action needed beyond just
     * event verification.  One good use is to ensure that specific
     * services are actually in the services registry or validating
     * the service properties.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void validate(BundleContext testContext) throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences(interfaceName, null);
        assertNotNull("No " + interfaceName + " service located for bundle " + bundle.getSymbolicName(), refs);
        // We might have services registered from multiple bundles, so scan this list
        // looking for one from the target bundle and validate that information.
        for (int i = 0; i < refs.length; i++) {
            if (bundle == refs[i].getBundle()) {
                validateProperties(refs[0]);
                return;
            }
        }
        // none found with the target bundle
        fail("No " + interfaceName + " service located for bundle " + bundle.getSymbolicName());
    }


    protected void validateProperties(ServiceReference ref) {
        assertEquals("Property osgi.service.blueprint.compname", "ServiceOne", ref.getProperty("osgi.service.blueprint.compname"));
        assertNull("Property service.ranking", ref.getProperty("service.ranking"));
        assertEquals("Property test.case.property.int", Integer.valueOf(1), ref.getProperty("test.case.property.int"));
        assertEquals("Property test.case.property.short", Short.valueOf((short)2), ref.getProperty("test.case.property.short"));
        assertEquals("Property test.case.property.byte", Byte.valueOf((byte)3), ref.getProperty("test.case.property.byte"));
        assertEquals("Property test.case.property.char", Character.valueOf('4'), ref.getProperty("test.case.property.char"));
        assertEquals("Property test.case.property.long", Long.valueOf(5), ref.getProperty("test.case.property.long"));
        assertEquals("Property test.case.property.boolean", Boolean.TRUE, ref.getProperty("test.case.property.boolean"));
		assertEquals("Property test.case.property.float", Float.valueOf(6.0f),
				ref.getProperty("test.case.property.float"));
        assertEquals("Property test.case.property.double", Double.valueOf(7.0), ref.getProperty("test.case.property.double"));

        assertEquals("Property test.case.property.java.lang.Integer", Integer.valueOf(11), ref.getProperty("test.case.property.java.lang.Integer"));
        assertEquals("Property test.case.property.java.lang.Short", Short.valueOf((short)12), ref.getProperty("test.case.property.java.lang.Short"));
        assertEquals("Property test.case.property.java.lang.Byte", Byte.valueOf((byte)13), ref.getProperty("test.case.property.java.lang.Byte"));
        assertEquals("Property test.case.property.java.lang.Char", Character.valueOf('a'), ref.getProperty("test.case.property.java.lang.Character"));
        assertEquals("Property test.case.property.java.lang.Long", Long.valueOf(15), ref.getProperty("test.case.property.java.lang.Long"));
        assertEquals("Property test.case.property.java.lang.Boolean", Boolean.FALSE, ref.getProperty("test.case.property.java.lang.Boolean"));
		assertEquals("Property test.case.property.java.lang.Float",
				Float.valueOf(16.0f),
				ref.getProperty("test.case.property.java.lang.Float"));
        assertEquals("Property test.case.property.java.lang.Double", Double.valueOf(17.0), ref.getProperty("test.case.property.java.lang.Double"));

        assertEquals("Property test.case.property.int.array", 1, ((int[])ref.getProperty("test.case.property.int.array"))[0]);
        assertEquals("Property test.case.property.short.array", 2, ((short[])ref.getProperty("test.case.property.short.array"))[0]);
        assertEquals("Property test.case.property.byte.array", 3, ((byte[])ref.getProperty("test.case.property.byte.array"))[0]);
        assertEquals("Property test.case.property.char.array", '4', ((char[])ref.getProperty("test.case.property.char.array"))[0]);
        assertEquals("Property test.case.property.long.array", 5, ((long[])ref.getProperty("test.case.property.long.array"))[0]);
        assertEquals("Property test.case.property.boolean.array", true, ((boolean[])ref.getProperty("test.case.property.boolean.array"))[0]);
        assertEquals("Property test.case.property.float.array", 6.0, ((float[])ref.getProperty("test.case.property.float.array"))[0], 0.0);
        assertEquals("Property test.case.property.double.array", 7.0, ((double[])ref.getProperty("test.case.property.double.array"))[0], 0.0);

        assertEquals("Property test.case.property.java.lang.String.array", "abc", ((Object[])ref.getProperty("test.case.property.java.lang.String.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Integer.array", Integer.valueOf(11), ((Object[])ref.getProperty("test.case.property.java.lang.Integer.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Short.array", Short.valueOf((short)12), ((Object[])ref.getProperty("test.case.property.java.lang.Short.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Byte.array", Byte.valueOf((byte)13), ((Object[])ref.getProperty("test.case.property.java.lang.Byte.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Char.array", Character.valueOf('a'), ((Object[])ref.getProperty("test.case.property.java.lang.Character.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Long.array", Long.valueOf(15), ((Object[])ref.getProperty("test.case.property.java.lang.Long.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Boolean.array", Boolean.FALSE, ((Object[])ref.getProperty("test.case.property.java.lang.Boolean.array"))[0]);
		assertEquals("Property test.case.property.java.lang.Float.array",
				Float.valueOf(16.0f), ((Object[]) ref.getProperty(
						"test.case.property.java.lang.Float.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Double.array", Double.valueOf(17.0), ((Object[])ref.getProperty("test.case.property.java.lang.Double.array"))[0]);

        assertEquals("Property test.case.property.java.lang.String.Set", "abc", ((Set)ref.getProperty("test.case.property.java.lang.String.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Integer.Set", Integer.valueOf(11), ((Set)ref.getProperty("test.case.property.java.lang.Integer.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Short.Set", Short.valueOf((short)12), ((Set)ref.getProperty("test.case.property.java.lang.Short.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Byte.Set", Byte.valueOf((byte)13), ((Set)ref.getProperty("test.case.property.java.lang.Byte.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Char.Set", Character.valueOf('a'), ((Set)ref.getProperty("test.case.property.java.lang.Character.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Long.Set", Long.valueOf(15), ((Set)ref.getProperty("test.case.property.java.lang.Long.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Boolean.Set", Boolean.FALSE, ((Set)ref.getProperty("test.case.property.java.lang.Boolean.Set")).toArray()[0]);
		assertEquals("Property test.case.property.java.lang.Float.Set",
				Float.valueOf(16.0f),
				((Set) ref
						.getProperty("test.case.property.java.lang.Float.Set"))
								.toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Double.Set", Double.valueOf(17.0), ((Set)ref.getProperty("test.case.property.java.lang.Double.Set")).toArray()[0]);

        assertEquals("Property test.case.property.java.lang.String.List", "abc", ((List)ref.getProperty("test.case.property.java.lang.String.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Integer.List", Integer.valueOf(11), ((List)ref.getProperty("test.case.property.java.lang.Integer.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Short.List", Short.valueOf((short)12), ((List)ref.getProperty("test.case.property.java.lang.Short.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Byte.List", Byte.valueOf((byte)13), ((List)ref.getProperty("test.case.property.java.lang.Byte.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Char.List", Character.valueOf('a'), ((List)ref.getProperty("test.case.property.java.lang.Character.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Long.List", Long.valueOf(15), ((List)ref.getProperty("test.case.property.java.lang.Long.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Boolean.List", Boolean.FALSE, ((List)ref.getProperty("test.case.property.java.lang.Boolean.List")).get(0));
		assertEquals("Property test.case.property.java.lang.Float.List",
				Float.valueOf(16.0f),
				((List) ref
						.getProperty("test.case.property.java.lang.Float.List"))
								.get(0));
        assertEquals("Property test.case.property.java.lang.Double.List", Double.valueOf(17.0), ((List)ref.getProperty("test.case.property.java.lang.Double.List")).get(0));
    }
}


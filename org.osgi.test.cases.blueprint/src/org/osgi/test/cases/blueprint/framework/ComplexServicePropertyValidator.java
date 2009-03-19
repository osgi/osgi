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

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.TestServiceOne;

/**
 * A one-off test validator to check the values of
 * a very complex service property set used for
 * export.
 */
public class ComplexServicePropertyValidator extends Assert implements TestValidator, BundleAware {
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
        assertEquals("Property test.case.property.int", new Integer(1), ref.getProperty("test.case.property.int"));
        assertEquals("Property test.case.property.short", new Short((short)2), ref.getProperty("test.case.property.short"));
        assertEquals("Property test.case.property.byte", new Byte((byte)3), ref.getProperty("test.case.property.byte"));
        assertEquals("Property test.case.property.char", new Character('4'), ref.getProperty("test.case.property.char"));
        assertEquals("Property test.case.property.long", new Long((long)5), ref.getProperty("test.case.property.long"));
        assertEquals("Property test.case.property.boolean", Boolean.TRUE, ref.getProperty("test.case.property.boolean"));
        assertEquals("Property test.case.property.float", new Float(6.0), ref.getProperty("test.case.property.float"));
        assertEquals("Property test.case.property.double", new Double(7.0), ref.getProperty("test.case.property.double"));

        assertEquals("Property test.case.property.java.lang.Integer", new Integer(11), ref.getProperty("test.case.property.java.lang.Integer"));
        assertEquals("Property test.case.property.java.lang.Short", new Short((short)12), ref.getProperty("test.case.property.java.lang.Short"));
        assertEquals("Property test.case.property.java.lang.Byte", new Byte((byte)13), ref.getProperty("test.case.property.java.lang.Byte"));
        assertEquals("Property test.case.property.java.lang.Char", new Character('a'), ref.getProperty("test.case.property.java.lang.Character"));
        assertEquals("Property test.case.property.java.lang.Long", new Long((long)15), ref.getProperty("test.case.property.java.lang.Long"));
        assertEquals("Property test.case.property.java.lang.Boolean", Boolean.FALSE, ref.getProperty("test.case.property.java.lang.Boolean"));
        assertEquals("Property test.case.property.java.lang.Float", new Float(16.0), ref.getProperty("test.case.property.java.lang.Float"));
        assertEquals("Property test.case.property.java.lang.Double", new Double(17.0), ref.getProperty("test.case.property.java.lang.Double"));

        assertEquals("Property test.case.property.int.array", 1, ((int[])ref.getProperty("test.case.property.int.array"))[0]);
        assertEquals("Property test.case.property.short.array", 2, ((short[])ref.getProperty("test.case.property.short.array"))[0]);
        assertEquals("Property test.case.property.byte.array", 3, ((byte[])ref.getProperty("test.case.property.byte.array"))[0]);
        assertEquals("Property test.case.property.char.array", '4', ((char[])ref.getProperty("test.case.property.char.array"))[0]);
        assertEquals("Property test.case.property.long.array", (long)5, ((long[])ref.getProperty("test.case.property.long.array"))[0]);
        assertEquals("Property test.case.property.boolean.array", true, ((boolean[])ref.getProperty("test.case.property.boolean.array"))[0]);
        assertEquals("Property test.case.property.float.array", 6.0, (double)((float[])ref.getProperty("test.case.property.float.array"))[0], 0.0);
        assertEquals("Property test.case.property.double.array", 7.0, ((double[])ref.getProperty("test.case.property.double.array"))[0], 0.0);

        assertEquals("Property test.case.property.java.lang.String.array", "abc", ((Object[])ref.getProperty("test.case.property.java.lang.Integer.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Integer.array", new Integer(11), ((Object[])ref.getProperty("test.case.property.java.lang.Integer.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Short.array", new Short((short)12), ((Object[])ref.getProperty("test.case.property.java.lang.Short.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Byte.array", new Byte((byte)13), ((Object[])ref.getProperty("test.case.property.java.lang.Byte.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Char.array", new Character('a'), ((Object[])ref.getProperty("test.case.property.java.lang.Character.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Long.array", new Long((long)15), ((Object[])ref.getProperty("test.case.property.java.lang.Long.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Boolean.array", Boolean.FALSE, ((Object[])ref.getProperty("test.case.property.java.lang.Boolean.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Float.array", new Float(16.0), ((Object[])ref.getProperty("test.case.property.java.lang.Float.array"))[0]);
        assertEquals("Property test.case.property.java.lang.Double.array", new Double(17.0), ((Object[])ref.getProperty("test.case.property.java.lang.Double.array"))[0]);

        assertEquals("Property test.case.property.java.lang.String.Set", "abc", ((Set)ref.getProperty("test.case.property.java.lang.Integer.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Integer.Set", new Integer(11), ((Set)ref.getProperty("test.case.property.java.lang.Integer.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Short.Set", new Short((short)12), ((Set)ref.getProperty("test.case.property.java.lang.Short.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Byte.Set", new Byte((byte)13), ((Set)ref.getProperty("test.case.property.java.lang.Byte.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Char.Set", new Character('a'), ((Set)ref.getProperty("test.case.property.java.lang.Character.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Long.Set", new Long((long)15), ((Set)ref.getProperty("test.case.property.java.lang.Long.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Boolean.Set", Boolean.FALSE, ((Set)ref.getProperty("test.case.property.java.lang.Boolean.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Float.Set", new Float(16.0), ((Set)ref.getProperty("test.case.property.java.lang.Float.Set")).toArray()[0]);
        assertEquals("Property test.case.property.java.lang.Double.Set", new Double(17.0), ((Set)ref.getProperty("test.case.property.java.lang.Double.Set")).toArray()[0]);

        assertEquals("Property test.case.property.java.lang.String.List", "abc", ((List)ref.getProperty("test.case.property.java.lang.Integer.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Integer.List", new Integer(11), ((List)ref.getProperty("test.case.property.java.lang.Integer.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Short.List", new Short((short)12), ((List)ref.getProperty("test.case.property.java.lang.Short.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Byte.List", new Byte((byte)13), ((List)ref.getProperty("test.case.property.java.lang.Byte.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Char.List", new Character('a'), ((List)ref.getProperty("test.case.property.java.lang.Character.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Long.List", new Long((long)15), ((List)ref.getProperty("test.case.property.java.lang.Long.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Boolean.List", Boolean.FALSE, ((List)ref.getProperty("test.case.property.java.lang.Boolean.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Float.List", new Float(16.0), ((List)ref.getProperty("test.case.property.java.lang.Float.List")).get(0));
        assertEquals("Property test.case.property.java.lang.Double.List", new Double(17.0), ((List)ref.getProperty("test.case.property.java.lang.Double.List")).get(0));
    }
}


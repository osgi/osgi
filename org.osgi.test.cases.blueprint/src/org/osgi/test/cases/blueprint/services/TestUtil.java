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

package org.osgi.test.cases.blueprint.services;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public class TestUtil {
    /**
     * Perform a comparison to determine if all properties in an
     * expected set are contained in a received dictionary.
     *
     * @param expected The expected set of properties.
     * @param received The received property set.
     *
     * @return true if all of the elements in the expected Map are contained
     *         in the received Map.  Note that is not the same as Map.equals(),
     *         as the received Map may contain information not required by the
     *         expected Map.
     */
    public static boolean containsAll(Map expected, Map received) {
        Iterator keys = expected.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            Object expectedValue = expected.get(key);
            Object receivedValue = received.get(key);
            // if not in the received dictionary, this is a false test
            if (receivedValue == null) {
//              System.out.println("++++++++++ Missing property " + key + " in [" + formatProperties(received) + "]");
                return false;
            }
            // if the values don't compare, this is a failure too
            if (!expectedValue.equals(receivedValue)) {
                return false;
            }
        }
        // good match
        return true;
    }


    /**
     * Validate a set of properties, throwing an exception detailing the first
     * mismatch.
     *
     * @param expected The expected set of properties.
     * @param received The received property set.
     */
    public static void validateProperties(Map expected, Map received) {
        Iterator keys = expected.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            Object expectedValue = expected.get(key);
            Object receivedValue = received.get(key);
            // if not in the received dictionary, this is a false test
            if (receivedValue == null) {
                throw new AssertionFailedError("Missing property " + key + " in [" + formatProperties(received) + "]");
            }
            // if the values don't compare, this is a failure too
            if (!expectedValue.equals(receivedValue)) {
                throw new AssertionFailedError("Mismatched property value for key=" + key + ", expected=" + expectedValue + ", received=" + receivedValue);
            }
        }
    }


    /**
     * Perform a comparison to determine if all properties in an
     * expected set are contained in a ServiceReference.
     *
     * @param expected The expected set of properties.
     * @param received The service reference.
     *
     * @return true if all of the elements in the expected dictionary are contained
     *         in the received ServiceReference.
     */
    public static boolean containsAll(Dictionary expected, ServiceReference received) {
        Enumeration keys = expected.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            Object expectedValue = expected.get(key);
            Object receivedValue = received.getProperty(key);
            // if not in the received dictionary, this is a false test
            if (receivedValue == null) {
                return false;
            }
            // if the values don't compare, this is a failure too
            if (!expectedValue.equals(receivedValue)) {
                return false;
            }
        }
        // good match
        return true;
    }


    /**
     * Scrape all of the service properties from a ServiceReference.
     *
     * @param received The service reference.
     *
     * @return A map containing all of the property values.
     */
    public static Map getProperties(ServiceReference received) {
        Map result = new HashMap();
        String[] keys = received.getPropertyKeys();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], received.getProperty(keys[i]));
        }
        return result;
    }


    /**
     * Validate a bundle version identifier
     *
     * @param v      The version string to match against.
     *
     * @return true if the versions match, false otherwise.
     */
    public static boolean validateBundleVersion(Bundle source, String v) {
		String myBundleVersion = (String)source.getHeaders().get(Constants.BUNDLE_VERSION);
        // handle the null cases
        if (myBundleVersion == null) {
            return v == null ? true : false;
        }
        if (v == null) {
            return false;
        }
        // and compare the strings.
        return myBundleVersion.equals(v);
    }


    /**
     * Validate a bundle version identifier
     *
     * @param v      The version value to match against.
     *
     * @return true if the versions match, false otherwise.
     */
    public static boolean validateBundleVersion(Bundle source, Version v) {
        // if there is nothing there, this is a failure for the tests
        if (v == null) {
            return false;
        }

		String myVersionString = (String)source.getHeaders().get(Constants.BUNDLE_VERSION);

        Version myBundleVersion;

        try {
            myBundleVersion = Version.parseVersion(myVersionString);
        } catch (IllegalArgumentException e) {
            // can't validate this
            return false;
        }

        // and compare the versions
        return myBundleVersion.equals(v);
    }

    /**
     * Validate a bundle symbolic name
     *
     * @param v      The symbolic name to match against
     *
     * @return true if the versions match, false otherwise.
     */
    public static boolean validateBundleSymbolicName(Bundle source, String v) {
		String myBundleVersion = source.getSymbolicName();
        if (v == null) {
            return false;
        }
        // and compare the strings.
        return myBundleVersion.equals(v);
    }

    /**
     * Validate a bundle id
     *
     * @param id     The bundle id value passed on the event.
     *
     * @return true if the ids match, false otherwise.
     */
    public static boolean validateBundleId(Bundle source, Long id) {
        // missing from the event.
        if (id == null) {
            return false;
        }
        // compare the values
        return source.getBundleId() == id.longValue();
    }

    /**
     * Format the expected properties into string form.
     *
     * @return The string containing the property values.
     */
    static public String formatProperties(Map props) {
        StringBuffer buffer = new StringBuffer();
        Iterator i = props.keySet().iterator();
        while (i.hasNext()) {
            String name = (String)i.next();
            buffer.append(name);
            buffer.append("=");
            buffer.append(props.get(name).toString());
            buffer.append(", ");
        }
        // remove the trailing comma
        buffer.deleteCharAt(buffer.length() - 2);
        return buffer.toString();
    }


    static public boolean contains(String target, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (target.equals(array[i])) {
                return true;
            }
        }
        return false;
    }
}


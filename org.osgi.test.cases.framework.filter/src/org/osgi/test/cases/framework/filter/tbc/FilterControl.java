/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.filter.tbc;

import java.io.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.test.cases.util.*;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Radio Systems AB
 */
public class FilterControl extends DefaultTestBundleControl {
	Bundle								tb1;
	public static ServiceRegistration	serviceA;
	public static Dictionary			serviceAProperties;
	static String[]						methods	= new String[] {"prepare",
			"test_framework_filter_m1", "test_framework_filter_m2",
			"test_framework_filter_m3", "test_framework_filter_m4",
			"test_framework_filter_m5", "test_framework_filter_m6",
			"test_framework_filter_m9", "test_framework_filter_m10",
			"test_framework_filter_s1",			};

	public String[] getMethods() {
		return methods;
	}

	public void prepare() throws Exception {
		tb1 = installBundle("tb1.jar");
		tb1.start();
	}

	public void testFilter(String filter, String logtext) throws Exception {
		ServiceReference refs[];
		refs = getContext().getServiceReferences(
				"org.osgi.test.cases.framework.filter.tb1.TestService", filter);
		logRefList(logtext, refs);
	}

	/**
	 * Tests a null filter.
	 */
	public void test_framework_filter_m1() throws Exception {
		testFilter(null, "Testing null filter");
	}

	/**
	 * Tests a syntactically invalid expression.
	 */
	public void test_framework_filter_m2() throws Exception {
		ServiceReference refs[];
		String res;
		try {
			refs = getContext()
					.getServiceReferences(
							"org.osgi.test.cases.framework.filter.tb1.TestService",
							"(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(!(name=ServiceA))))))))))))))))))))))))");
			res = "No exception thrown, Error!";
		}
		catch (InvalidSyntaxException ise) {
			res = "Exception thrown, Ok.";
		}
		log("Testing invalid syntax", res);
	}

	/**
	 * Tests case insensitivity in attribute names.
	 */
	public void test_framework_filter_m3() throws Exception {
		testFilter("(nAMe=ServiceA)", "Testing case insensitive");
	}

	/**
	 * Tests basic filter functionality.
	 * 
	 * @requirement framework.filter.m4
	 */
	public void test_framework_filter_m4() throws Exception {
		testFilter(
				"(  \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F |  (   \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  name 	  =ServiceA)(  \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  name=ServiceB)   \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  )",
				"Testing spaces in filter operation");
	}

	/**
	 * Tests String types.
	 */
	public void test_framework_filter_m5() throws Exception {
		testFilter("(name=ServiceA)", "Testing type String");
	}

	/**
	 * Tests numeric types.
	 */
	public void test_framework_filter_m6() throws Exception {
		testFilter("(Integer=3)", "Testing type Integer");
		testFilter("(Long=3)", "Testing type Long");
		testFilter("(Byte=3)", "Testing type Byte");
		testFilter("(Short=3)", "Testing type Short");
	}

	/**
	 * Test that a value is found in an array of values.
	 * 
	 * @requirement framework.filter.m9
	 */
	public void test_framework_filter_m9() throws Exception {
		testFilter("(compatible=1.5)", "Testing array filter");
	}

	/**
	 * Test that a value is found in a Vector of values.
	 * 
	 * @requirement framework.filter.m10
	 */
	public void test_framework_filter_m10() throws Exception {
		testFilter("(compatible=2.1)", "Testing Vector filter");
	}

	/**
	 * Tests the approximate (~=) operation. Case and white space differences
	 * should match.
	 */
	public void test_framework_filter_s1() throws Exception {
		testFilter("(name~=servicea)", "Testing approximate equality (case)");
		testFilter("(description~=Service A)",
				"Testing approximate equality (white space)");
	}

	/**
	 * Help function used to sort and log an array of service references.
	 */
	public void logRefList(String prefix, ServiceReference refs[])
			throws IOException {
		StringBuffer logString = new StringBuffer();
		if (refs != null) {
			Vector services = new Vector();
			logString.append(refs.length + " services: { ");
			// The order of the services isn't specified, so we'd better sort
			// them.
			outer: for (int i = 0; i < refs.length; i++) {
				String descr = refs[i].getProperty("name") + " "
						+ refs[i].getProperty("version");
				for (i = 0; i < services.size(); i++) {
					if (((String) services.elementAt(i)).compareTo(descr) >= 0) {
						services.insertElementAt(descr, i);
						continue outer;
					}
				}
				services.addElement(descr);
			}
			boolean comma = false;
			for (Enumeration e = services.elements(); e.hasMoreElements();) {
				if (comma)
					logString.append(", ");
				logString.append(e.nextElement().toString());
				comma = true;
			}
			logString.append(" }");
		}
		else
			logString.append("No services.");
		log(prefix, logString.toString());
	}
}

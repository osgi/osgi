/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.filter.tbc;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

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
			"test_framework_filter_s1",	 "test_framework_filter_s2",	
			"test_framework_filter_s3",	 "test_framework_filter_s4",	
			"test_framework_filter_s5",	 "test_framework_filter_s6",	
			"test_framework_filter_s7",	 "test_framework_filter_s8",	
			"test_framework_filter_s9"	};

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
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m1() throws Exception {
		testFilter(null, "Testing null filter");
	}

	/**
	 * Tests a syntactically invalid expression.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
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
		log("Testing invalid syntax" + res);
	}

	/**
	 * Tests case insensitivity in attribute names.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m3() throws Exception {
		testFilter("(nAMe=ServiceA)", "Testing case insensitive");
	}

	/**
	 * Tests basic filter functionality.
	 * 
	 * @requirement framework.filter.m4
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m4() throws Exception {
		testFilter(
				"(  \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F |  (   \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  name 	  =ServiceA)(  \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  name=ServiceB)   \t \r \n \u000B \u000C \u001C \u001D  \u001E \u001F  )",
				"Testing spaces in filter operation");
	}

	/**
	 * Tests String types.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m5() throws Exception {
		testFilter("(name=ServiceA)", "Testing type String");
	}

	/**
	 * Tests numeric types.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
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
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m9() throws Exception {
		testFilter("(compatible=1.5)", "Testing array filter");
	}

	/**
	 * Test that a value is found in a Vector of values.
	 * 
	 * @requirement framework.filter.m10
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_m10() throws Exception {
		testFilter("(compatible=2.1)", "Testing Vector filter");
	}

	/**
	 * Tests the approximate (~=) operation. Case and white space differences
	 * should match.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s1() throws Exception {
		testFilter("(name~=servicea)", "Testing approximate equality (case)");
		testFilter("(description~=Service A)",
				"Testing approximate equality (white space)");
	}

	/**
	 * Filter by an object that implements java.lang.Comparable
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s2() throws Exception {
		// Always finds the service object when filtering by ObjectA
		testFilter("(ObjectA=0)",
				"Testing object that implements java.lang.Comparable");
	}

	/**
	 * Testing = operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor. 
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s3() throws Exception {
		testFilter("(ObjectB=4)",
				"Testing = operator with object that does not implement java.lang.Comparable");
	}

	/**
	 * Testing <= operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s4() throws Exception {
		testFilter("(ObjectB<=4)",
				"Testing <= operator with object that does not implement java.lang.Comparable");
	}

	/**
	 * Testing >= operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s5() throws Exception {
		testFilter("(ObjectB>=4)",
				"Testing >= operator with object that does not implement java.lang.Comparable");
	}

	/**
	 * Testing ~= operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s6() throws Exception {
		testFilter("(ObjectB~=4)",
				"Testing ~= operator with object that does not implement java.lang.Comparable");
	}

	/**
	 * Testing < operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s7() throws Exception {
		String msg;
		msg = "Testing < operator with object that does not implement java.lang.Comparable";
		try {
			testFilter("(ObjectB<4)", msg);
		}
		catch (InvalidSyntaxException ise) {
			log(msg + ": Exception thrown, Ok.");
		}
	}

	/**
	 * Testing > operator with an object that does not implement
	 * <code>java.lang.Comparable</code> but has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s8() throws Exception {
		String msg;
		msg = "Testing > operator with object that does not implement java.lang.Comparable";
		try {
			testFilter("(ObjectB>4)", msg);
		}
		catch (InvalidSyntaxException ise) {
			log(msg + ": Exception thrown, Ok.");
		}
	}

	/**
	 * Testing with an object that neither implements
	 * <code>java.lang.Comparable</code> nor has a special constructor.
	 * 
	 * @spec BundleContext.getServiceReferences(String, String)
	 */
	public void test_framework_filter_s9() throws Exception {
		testFilter(
				"(ObjectC=4)",
				"Testing object that does not have a public constructor with a single java.lang.String argument");
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
		log(prefix + logString.toString());
	}
}

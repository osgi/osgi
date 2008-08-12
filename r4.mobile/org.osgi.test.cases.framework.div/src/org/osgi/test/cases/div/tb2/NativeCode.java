/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.div.tb2; // could not rename this package without rebuilding native so/dll

import org.osgi.framework.*;

/**
 * Bundle for the NativeCode test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class NativeCode implements BundleActivator {
	static boolean	initOk	= false;
	/**
	 * Static initializer to load the native library.
	 */
	static {
		try {
			System.loadLibrary("Native");
			initOk = true;
		}
		catch (UnsatisfiedLinkError ule) {
			// initOk will remain false.
		}
	}

	/**
	 * The native method.
	 */
	public native void count(int i);

	/**
	 * Starts the bundle. Excercises the native code.
	 */
	public void start(BundleContext bc) throws BundleException {
		test();
	}

	public static void test() throws BundleException {
		NativeCode n = new NativeCode();
		if (initOk) {
			n.count(10000000);
		}
		else
			throw new BundleException("Native code not initialized.");
	}
	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}

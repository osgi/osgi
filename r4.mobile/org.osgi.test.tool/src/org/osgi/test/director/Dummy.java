/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.util.Dictionary;
import org.osgi.test.service.TestCase;
import org.osgi.test.shared.IApplet;

/**
 * IMplement a dummy UI.
 * 
 * This class implements the IApplet interface that is used to have a UI. If no
 * UI exists, this dummy can be instantiated and used instead. This saves a lot
 * of null checks.
 */
class Dummy implements IApplet {
	public void setMessage(String s) {
		System.out.println(":: " + s);
	}

	public void setError(String s) {
	}

	public void setProgress(int prec) {
	}

	public void setResult(TestCase id, int n) {
	}

	public void setTargetProperties(Dictionary d) {
	}

	public void finished() {
	}

	public void bundlesChanged() {
	}

	public void targetsChanged() {
	}

	public void casesChanged() {
	}

	public void alive(String msg) {
	}

	public void step(String msg) {
	}
}

package org.osgi.test.cases.util;

/**
 * Thrown when an assertion failed.
 */
public class AssertionFailedError extends Error {

	public AssertionFailedError () {
	}
	public AssertionFailedError (String message) {
		super (message);
	}
}
/*
 * Copyright (c) 2008 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.distribution.common;


/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class TestServiceImpl implements A, B {

	/**
	 * @see org.osgi.test.cases.distribution.common.A#getA()
	 */
	public String getA() {
		return "A";
	}

	/**
	 * @see org.osgi.test.cases.distribution.common.B#getB()
	 */
	public String getB() {
		return "B";
	}

}

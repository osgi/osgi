/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.applet;

import netscape.application.Bitmap;
import netscape.util.*;

/**
 * A hack to allow loading of bitmaps from resources.
 * 
 * Netscape Bitmaps assume they load the images form the codebase. However,
 * Netscape does not support the resource URL format. This private bitmap works
 * in conjunction with the TestApplet to get the image from the jar file. Ie
 * this is a workaround.
 */
public class PrivateBitmap extends Bitmap {
	/**
	 * For Serialization purpose.
	 */
	public PrivateBitmap() {
	}

	/**
	 * Overrides decode to be able to get the image from the resources via
	 * TestApplet. We ignore names that start with netscape because they are
	 * somehow built in.
	 */
	public void decode(Decoder decoder) throws CodingException {
		super.decode(decoder);
		if (name().startsWith("netscape"))
			return;
		Bitmap tmp = TestApplet.getBitmap(name());
		decoder.replaceObject(tmp);
	}
}

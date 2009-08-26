package org.osgi.test.cases.permissionadmin.junit;

import java.util.ResourceBundle;

public class SignatureResource {

	private static final String			BUNDLE_NAME		= "org.osgi.test.cases.permissionadmin.junit.signature";

	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle
																.getBundle(BUNDLE_NAME);

	private SignatureResource() {
		// empty
	}

	public static String getString(String key) {
		return RESOURCE_BUNDLE.getString(key);
	}
}

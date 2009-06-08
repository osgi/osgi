

package org.osgi.test.cases.permissionadmin.signature.tbc;

import java.util.ResourceBundle;


public class SignatureResource {
	
	private static final String BUNDLE_NAME = "org.osgi.test.cases.permissionadmin.signature.tbc.signature";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private SignatureResource() {
	}

	public static String getString(String key) {
		return RESOURCE_BUNDLE.getString(key);
	}
	
}
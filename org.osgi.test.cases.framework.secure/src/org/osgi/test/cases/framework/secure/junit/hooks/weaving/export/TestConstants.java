package org.osgi.test.cases.framework.secure.junit.hooks.weaving.export;

import org.osgi.test.support.OSGiTestCaseProperties;

public class TestConstants {
	public static final String DYNAMIC_IMPORT_PACKAGE = "org.osgi.resource";
	public static final String DYNAMIC_IMPORT_CLASS = DYNAMIC_IMPORT_PACKAGE + ".Resource";
	
	public static final String PROP_PREFIX = "org.osgi.test.cases.framework.secure.weaving.";
	public static final String PROP_EXPECT_SECURITYEXCEPTION = PROP_PREFIX + "expectSecurityException";
	public static final String PROP_EXPECT_SECURITYEXCEPTION_ALL = PROP_EXPECT_SECURITYEXCEPTION + ".all";
	public static final String PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT = PROP_EXPECT_SECURITYEXCEPTION + ".addDynamicImport";
	public static final String PROP_INVALID_SETBYTES = PROP_PREFIX + "invalidSetBytes";
	public static final String PROP_RETHROW_SECURITYEXCEPTION = PROP_PREFIX + "rethrowSecurityException";
	
	public static final String WOVEN_CLASS = "org.osgi.test.cases.framework.secure.weaving.tb.woven.WeaveMe";
	
	public static boolean isExpectSecurityExceptionAll() {
		return OSGiTestCaseProperties.getBooleanProperty(
				PROP_EXPECT_SECURITYEXCEPTION_ALL, false);
	}
	
	public static boolean isExpectSecurityExceptionAddDynamicImport() {
		return OSGiTestCaseProperties.getBooleanProperty(
				PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT, false);
	}
	
	public static boolean isInvalidSetBytes() {
		return OSGiTestCaseProperties.getBooleanProperty(PROP_INVALID_SETBYTES,
				false);
	}
	
	public static boolean isRethrowingSecurityException() {
		return OSGiTestCaseProperties.getBooleanProperty(
				PROP_RETHROW_SECURITYEXCEPTION, false);
	}
}

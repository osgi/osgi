package org.osgi.test.cases.framework.secure.junit.hooks.weaving;

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
		return Boolean.getBoolean(PROP_EXPECT_SECURITYEXCEPTION_ALL);
	}
	
	public static boolean isExpectSecurityExceptionAddDynamicImport() {
		return Boolean.getBoolean(PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT);
	}
	
	public static boolean isInvalidSetBytes() {
		return Boolean.getBoolean(PROP_INVALID_SETBYTES);
	}
	
	public static boolean isRethrowingSecurityException() {
		return Boolean.getBoolean(PROP_RETHROW_SECURITYEXCEPTION);
	}
}

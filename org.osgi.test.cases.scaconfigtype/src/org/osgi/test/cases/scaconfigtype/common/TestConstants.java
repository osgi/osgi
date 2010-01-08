package org.osgi.test.cases.scaconfigtype.common;

public interface TestConstants {
	static final String ORG_OSGI_TEST_CASES_SCACONFIG_TYPE_BUNDLES = "org.osgi.test.cases.scaconfigtype.bundles";
	static final String STORAGEROOT = "org.osgi.test.cases.scaconfigtype.storageroot";

	static final String DEFAULT_STORAGEROOT = "generated/testframeworkstorage";
	
	static final String ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON = "org.osgi.test.cases.scaconfigtype.common";
	
	static final String BINDING_A_NCNAME = "serviceA";
	static final String SERVER_FRAMEWORK = "server";
	static final String CLIENT_FRAMEWORK = "client";
	static final long SERVICE_TIMEOUT = Long.getLong("org.osgi.test.cases.scaconfigtype.timeout", 5000);
}

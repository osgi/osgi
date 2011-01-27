/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.framework.weaving.tbx;

/**
 * Constants used in the Weaving Hook CT
 * 
 * @author IBM
 */
public interface TestConstants {

	/** The name of the basic test class for weaving */
	public static final String TEST_CLASS_NAME = "org.osgi.test.cases.framework.weaving.testclasses.TestClass";
	/** The name of the basic test class for weaving with a dynamic import */
	public static final String DYNAMIC_IMPORT_TEST_CLASS_NAME = "org.osgi.test.cases.framework.weaving.testclasses.TestDynamicImport";
	/** The name of the package containing the test classes for weaving */
	public static final String TESTCLASSES_PACKAGE = "org.osgi.test.cases.framework.weaving.testclasses";
	/** The symbolic name of the bundle containing the test classes for weaving */
	public static final String TESTCLASSES_SYM_NAME = "org.osgi.test.cases.framework.weaving.testclasses";
	/** The resource name of the bundle containing the test classes for weaving */
	public static final String TESTCLASSES_JAR = "testClasses.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	public static final String TEST_IMPORT_100_JAR = "testImport_1.0.0.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	public static final String TEST_IMPORT_110_JAR = "testImport_1.1.0.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	public static final String TEST_IMPORT_160_JAR = "testImport_1.6.0.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	public static final String TEST_ALT_IMPORT_JAR = "testAlternativeImport.jar";
	/** The symbolic name of one of the bundles containing SymbolicNameVersion class */
	public static final String TEST_IMPORT_SYM_NAME = "org.osgi.test.cases.framework.weaving.test.import";
	/** The symbolic name of one of the bundles containing SymbolicNameVersion class */
	public static final String TEST_ALT_IMPORT_SYM_NAME = "org.osgi.test.cases.framework.weaving.test.alternative.import";
	/** The package name for the SymbolicNameVersion class */
	public static final String IMPORT_TEST_CLASS_PKG = "org.osgi.test.cases.framework.weaving.imports";
	/** The class name name for the SymbolicNameVersion class */
	public static final String IMPORT_TEST_CLASS_NAME = IMPORT_TEST_CLASS_PKG + ".SymbolicNameVersion";
	
}

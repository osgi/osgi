/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.framework.div.tbc.Constants;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.test.service.TestCaseLink;

/**
 * Test the constants of the class org.osgi.framework.Constants.
 * 
 * @version $Revision$
 */
public class ConstantsValues {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new ConstantsValues
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home
	 */
	public ConstantsValues(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testConstantValues0001();
	}

	/**
	 * Test the constant values
	 */
	public void testConstantValues0001() throws Exception {
		if (!Constants.BUNDLE_SYMBOLICNAME.equals("Bundle-SymbolicName")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.SINGLETON_DIRECTIVE.equals("singleton")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.FRAGMENT_ATTACHMENT_ALWAYS.equals("always")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.FRAGMENT_ATTACHMENT_RESOLVETIME.equals("resolve-time")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.FRAGMENT_ATTACHMENT_NEVER.equals("never")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.BUNDLE_LOCALIZATION.equals("Bundle-Localization")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME.equals("OSGI-INF/l10n/bundle")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.REQUIRE_BUNDLE.equals("Require-Bundle")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.BUNDLE_VERSION_ATTRIBUTE.equals("bundle-version")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.FRAGMENT_HOST.equals("Fragment-Host")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.MULTIPLE_HOSTS_DIRECTIVE.equals("multiple-hosts")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.SELECTION_FILTER_ATTRIBUTE.equals("selection-filter")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.BUNDLE_MANIFESTVERSION.equals("Bundle-ManifestVersion")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.VERSION_ATTRIBUTE.equals("version")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE
				.equals("bundle-symbolic-name")) { //xxx
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.RESOLUTION_DIRECTIVE.equals("resolution")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.RESOLUTION_MANDATORY.equals("mandatory")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.RESOLUTION_OPTIONAL.equals("optional")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.USES_DIRECTIVE.equals("uses")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.INCLUDE_DIRECTIVE.equals("include")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.EXCLUDE_DIRECTIVE.equals("exclude")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.MANDATORY_DIRECTIVE.equals("mandatory")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.VISIBILITY_DIRECTIVE.equals("visibility")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.VISIBILITY_PRIVATE.equals("private")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.VISIBILITY_REEXPORT.equals("reexport")) {
			throw new ConstantsTestException("Testing constants");
		}

		if (!Constants.REEXPORT_PACKAGE.equals("Reexport-Package")) {
			throw new ConstantsTestException("Testing constants");
		}
	}
}
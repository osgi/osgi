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

package org.osgi.test.cases.framework.div.tbc.Bundle;

import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Test the method Bundle.getHeaders() and Bundle.getHeaders(Locale).
 * 
 * @author hmm@cesar.org.br
 * 
 * @version $Revision$
 */
public class GetHeaders {

	private BundleContext	_context;
	private String			_tcHome;
	private String[]		manifestHeadersKeys						= {
			"Bundle-Name", "Bundle-Description", "Bundle-Vendor",
			"Bundle-Version", "Bundle-DocURL", "Bundle-ContactAddress",
			"Bundle-Activator", "Bundle-Category", "Bundle-Copyright"};

	private String[]		tb1_manifestHeadersValues				= {
			"test.cases.framework.div.tb1",
			"Contains the manifest checked by the test case.",
			"Ericsson Radio Systems AB", "1.0.0", "http://www.ericsson.com",
			"info@ericsson.com",
			"org.osgi.test.cases.framework.div.tb1.CheckManifest",
			"should contain the bundle category",
			"should contain the bundle copyright"					};

	private String[]		tb8_manifestHeadersValues_default		= {
			"test.cases.framework.div.tb8",
			"Contains the manifest headers localized by bundle.properties test case.",
			"CESAR.ORG", "1.0", "http://www.cesar.org.br", "info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb8.CheckManifestGetHeaders",
			"Should contain the bundle category for tb8",
			"Should contain the bundle copyright for tb8"			};

	private String[]		tb9_manifestHeadersValues_en			= {
			"test.cases.framework.div.tb9",
			"Contains the manifest headers localized by bundle_en.properties test case.",
			"CESAR.ORG",
			"1.0",
			"http://www.cesar.org.br",
			"info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"			};

	private String[]		tb9_manifestHeadersValues_en_US			= {
			"test.cases.framework.div.tb9",
			"Contains the manifest headers localized by bundle_en_US.properties test case.",
			"CESAR.ORG",
			"1.0",
			"http://www.cesar.org.br",
			"info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"			};

	private String[]		tb9_manifestHeadersValues_rawHeaders	= {
			"%bundlename",
			"%bundledescription",
			"%bundlevendor",
			"1.0",
			"%docurl",
			"%contactinfo",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"%bundlecategory", "%bundlecopyright"					};

	private String[]		tb14_manifestHeadersValues_pt_BR		= {
			"test.cases.framework.div.tb14",
			"Contains the manifest headers localized by bundle_pt_BR.properties test case.",
			"CESAR.ORG", "1.0", "http://www.cesar.org.br", "info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb14.Activator",
			"Should contain the bundle category for tb14",
			"Should contain the bundle copyright for tb14"			};

	private String[]		tb14_manifestHeadersValues_rawHeaders	= {
			"bundlename", "bundledescription", "bundlevendor", "1.0", "docurl",
			"contactinfo", "org.osgi.test.cases.framework.div.tb14.Activator",
			"bundlecategory", "bundlecopyright"						};

	/**
	 * Creates a new GetHeaders
	 * 
	 * @param context content bundle context for this test, tcHome string with
	 *        test bundle home for this test
	 */
	public GetHeaders(BundleContext context, String tcHome) {
		_context = context;
		_tcHome = tcHome;

	}

	/**
	 * Run tests of this class
	 */
	public void run() throws Exception {
		Locale defaultLocale = Locale.getDefault();
		testGetHeaders001();
		testGetHeaders002();
		testGetHeaders003();
		testGetHeaders004();
		testGetHeaders005();
		testGetHeaders006();
		testGetHeaders007();
		testGetHeaders008();
		testGetHeaders009();
		testGetHeaders010();
		testGetHeaders011();
		testGetHeaders012();
		Locale.setDefault(defaultLocale);
	}

	/**
	 * Tests manifest headers localization for a bundle that does not have
	 * locale file.
	 */
	void testGetHeaders001() throws Exception {
		Bundle tb = _context.installBundle(_tcHome + "tb1.jar");
		tb.start();
		Dictionary h = tb.getHeaders();

		for (int i = 0; i < tb1_manifestHeadersValues.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb1_manifestHeadersValues[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb1_manifestHeadersValues[i]);
		}

		tb.stop();
		tb.uninstall();

	}

	/**
	 * Tests manifest headers localization for a bundle that has the default
	 * locale properties file, as defined by
	 * Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME.
	 */
	void testGetHeaders002() throws Exception {
		Bundle tb = _context.installBundle(_tcHome + "tb8.jar");
		tb.start();
		Dictionary h = tb.getHeaders();

		for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb8_manifestHeadersValues_default[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb8_manifestHeadersValues_default[i]);
		}

		tb.stop();
		tb.uninstall();
		// After the bundle has been uninstalled, it should return manifest
		// headers
		// localized for the default locale at the time the bundle was
		// uninstalled.
		h = tb.getHeaders();

		for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb8_manifestHeadersValues_default[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb8_manifestHeadersValues_default[i]);
		}
	}

	/**
	 * This method tests manifest headers localization for a bundle that has
	 * specific locale files including the default locale.
	 */
	void testGetHeaders003() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb = _context.installBundle(_tcHome + "tb9.jar");
		tb.start();
		Dictionary h = tb.getHeaders();

		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_en_US[i]);
		}

		// If the specified locale is null then the locale returned by
		// java.util.Locale.getDefault is used.
		h = tb.getHeaders(null);
		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_en_US[i]);
		}

		// If the specified locale is the empty string, this method
		// will return the raw (unlocalized) manifest headers including any
		// leading ‘%’
		h = tb.getHeaders("");

		for (int i = 0; i < tb9_manifestHeadersValues_rawHeaders.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_rawHeaders[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_rawHeaders[i]);
		}

		tb.stop();
		tb.uninstall();
		Locale.setDefault(new Locale("fr", "FR"));
		// After the bundle has been uninstalled, it should return manifest
		// headers
		// localized for the default locale at the time the bundle was
		// uninstalled.
		h = tb.getHeaders();
		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_en_US[i]);
		}

	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include the default locale.
	 */
	void testGetHeaders004() throws Exception {
		Locale.setDefault(new Locale("pt", "BR"));
		Bundle tb = _context.installBundle(_tcHome + "tb9.jar");
		tb.start();
		Dictionary h = tb.getHeaders();

		for (int i = 0; i < tb9_manifestHeadersValues_rawHeaders.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_rawHeaders[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_rawHeaders[i]);
		}

		tb.stop();
		tb.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that does not have
	 * locale file.
	 */
	void testGetHeaders005() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb = _context.installBundle(_tcHome + "tb1.jar");
		tb.start();

		Dictionary h = tb.getHeaders("en_US");
		for (int i = 0; i < tb1_manifestHeadersValues.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb1_manifestHeadersValues[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb1_manifestHeadersValues[i]);
		}

		tb.stop();
		tb.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has the default
	 * locale file, as defined by
	 * Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME.
	 */
	void testGetHeaders006() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb = _context.installBundle(_tcHome + "tb8.jar");
		tb.start();
		Dictionary h = tb.getHeaders("en_US");

		for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb8_manifestHeadersValues_default[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb8_manifestHeadersValues_default[i]);
		}

		tb.stop();
		tb.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files including the default locale.
	 */
	void testGetHeaders007() throws Exception {
		Bundle tb = _context.installBundle(_tcHome + "tb9.jar");
		tb.start();
		Dictionary h = tb.getHeaders("en");

		for (int i = 0; i < tb9_manifestHeadersValues_en.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_en[i]);
		}

		tb.stop();
		tb.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include locale requested.
	 */
	void testGetHeaders008() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb = _context.installBundle(_tcHome + "tb9.jar");
		tb.start();
		Dictionary h = tb.getHeaders("pt_BR");

		for (int i = 0; i < tb9_manifestHeadersValues_rawHeaders.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_rawHeaders[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_rawHeaders[i]);
		}

		tb.stop();
		tb.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include the default locale.
	 */
	void testGetHeaders009() throws Exception {
		Locale.setDefault(new Locale("pt", "BR"));
		Bundle tb = _context.installBundle(_tcHome + "tb9.jar");
		tb.start();
		Dictionary h = tb.getHeaders("en");

		for (int i = 0; i < tb9_manifestHeadersValues_en.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_en[i]);
		}

		tb.stop();
		tb.uninstall();
		Locale.setDefault(new Locale("en", "US"));
		// After the bundle has been uninstalled, it should return manifest
		// headers
		// localized for the default locale at the time the bundle was
		// uninstalled.
		h = tb.getHeaders("en");
		for (int i = 0; i < tb9_manifestHeadersValues_rawHeaders.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_rawHeaders[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_rawHeaders[i]);
		}
	}

	/**
	 * Tests manifest localization when bundle is not on default location. Tests
	 * manifest localization for a fragment bundle.
	 */
	void testGetHeaders010() throws Exception {
		Locale.setDefault(new Locale("pt", "BR"));
		//install host bundle
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		//install fragment bundle
		Bundle tb = _context.installBundle(_tcHome + "tb14.jar");

		//When searching for a localization file of a fragment bundle,
		//it must first look in the fragment’s host bundle (with the lowest
		// bundle id) and then look
		// in the host’s currently attached fragment bundles.
		Dictionary h = tb.getHeaders("pt_BR");

		for (int i = 0; i < tb14_manifestHeadersValues_pt_BR.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_pt_BR[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb14_manifestHeadersValues_pt_BR[i]);
		}

		h = tb.getHeaders("en_US");
		// manifest localization before bundle is resolved.
		for (int i = 0; i < tb14_manifestHeadersValues_rawHeaders.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_rawHeaders[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb14_manifestHeadersValues_rawHeaders[i]);
		}
		// resolve fragment bundle
		resolveBundle(tb);
		tb9.start();

		tb.uninstall();
		tb9.stop();
		tb9.uninstall();
	}

	/**
	 * Tests manifest localization for a host bundle.
	 */
	void testGetHeaders011() throws Exception {
		Bundle tb = _context.installBundle(_tcHome + "tb9.jar");
		Bundle tb14 = _context.installBundle(_tcHome + "tb14.jar");
		resolveBundle(tb14);
		tb.start();
		// When searching for a localization file of a host bundle,
		// it must first look in the bundle and then look in the currently
		// attached fragment bundles.
		Dictionary h = tb.getHeaders("pt_BR");

		for (int i = 0; i < tb14_manifestHeadersValues_pt_BR.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_pt_BR[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb14_manifestHeadersValues_pt_BR[i]);
		}

		tb.stop();
		tb.uninstall();
	}

	/**
	 * Tests manifest localization for a fragment bundle.
	 */
	void testGetHeaders012() throws Exception {
		
		//install host bundle
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		//install fragment bundle
		Bundle tb = _context.installBundle(_tcHome + "tb14.jar");

		//When searching for a localization file of a fragment bundle,
		//it must first look in the fragment’s host bundle (with the lowest
		// bundle id) and then look
		// in the host’s currently attached fragment bundles.

		//resolve fragment bundle
		resolveBundle(tb);
		tb9.start();
		// manifest localization after bundle is resolved.
		Dictionary h = tb.getHeaders("en_US");
		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_en_US[i]);
		}
		tb.uninstall();
		tb9.stop();
		tb9.uninstall();
	}

	/**
	 * Changes bundle state to Bundle.RESOLVED
	 * 
	 * @param bundle bundle
	 */
	private void resolveBundle(Bundle bundle) {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		// Get PackageAdmin service reference
		serviceReference = _context.getServiceReference(PackageAdmin.class
				.getName());
		packageAdmin = (PackageAdmin) _context.getService(serviceReference);

		// Resolve the fragment bundle
		packageAdmin.resolveBundles(new Bundle[] {bundle});

	}
}
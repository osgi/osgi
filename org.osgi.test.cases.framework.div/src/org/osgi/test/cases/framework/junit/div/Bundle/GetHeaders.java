/*
 * $Id$
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

package org.osgi.test.cases.framework.junit.div.Bundle;

import java.util.*;

import org.osgi.framework.*;

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
	private String[]		manifestHeadersKeys							= {
			"Bundle-Name", "Bundle-Description", "Bundle-Vendor",
			"Bundle-Version", "Bundle-DocURL", "Bundle-ContactAddress",
			"Bundle-Activator", "Bundle-Category", "Bundle-Copyright"	};

	private String[]		tb1_manifestHeadersValues					= {
			"test.cases.framework.div.tb1",
			"Contains the manifest checked by the test case.",
			"Ericsson Radio Systems AB",
			"Improper value for bundle manifest version 2",
			"http://www.ericsson.com", "info@ericsson.com",
			"org.osgi.test.cases.framework.div.tb1.CheckManifest",
			"should contain the bundle category",
			"should contain the bundle copyright"						};

	private String[]		tb8_manifestHeadersValues_default			= {
			"test.cases.framework.div.tb8",
			"Contains the manifest headers localized by bundle.properties test case.",
			"CESAR.ORG", "1.0", "http://www.cesar.org.br", "info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb8.CheckManifestGetHeaders",
			"Should contain the bundle category for tb8",
			"Should contain the bundle copyright for tb8"				};

	private String[]		tb9_manifestHeadersValues_en				= {
			"test.cases.framework.div.tb9",
			"Contains the manifest headers localized by bundle_en.properties test case.",
			"CESAR.ORG",
			"1.0",
			"http://www.cesar.org.br",
			"info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"				};

	private String[]		tb9_manifestHeadersValues_en_US				= {
			"test.cases.framework.div.tb9",
			"Contains the manifest headers localized by bundle_en_US.properties test case.",
			"CESAR.ORG",
			"1.0",
			"http://www.cesar.org.br",
			"info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"				};

	private String[]		tb9_manifestHeadersValues_rawHeaders		= {
			"%bundlename",
			"%bundledescription",
			"%bundlevendor",
			"1.0",
			"%docurl",
			"%contactinfo",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"%bundlecategory", "%bundlecopyright"						};

	private String[]		tb9_manifestHeadersValues_pt_BR				= {
			"test.cases.framework.div.tb9",
			"Contains the manifest headers localized by bundle_pt_BR.properties test case.",
			"CESAR.ORG",
			"1.0",
			"http://www.cesar.org.br",
			"info@cesar.org.br",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"Should contain the bundle category for tb9",
			"Should contain the bundle copyright for tb9"				};

	private String[]		tb9_manifestHeadersValues_missingLocale		= {
			"bundlename",
			"bundledescription",
			"bundlevendor",
			"1.0",
			"docurl",
			"contactinfo",
			"org.osgi.test.cases.framework.div.tb9.CheckManifestGetHeadersLocale",
			"bundlecategory", "bundlecopyright"							};

	private String[]		tb9_manifestHeadersKeys						= {
			"Bundle-Name", "Bundle-Description", "Bundle-Vendor",
			"Bundle-DocURL", "Bundle-ContactAddress", "Bundle-Category",
			"Bundle-Copyright",											};

	private String[]		tb14_manifestHeadersKeys					= {
			"Bundle-Name", "Bundle-Description", "Bundle-Vendor",
			"Bundle-Version", "Bundle-DocURL", "Bundle-ContactAddress",
			"Bundle-Category", "Bundle-Copyright"						};

	private String[]		tb14_manifestHeadersValues_en_US			= {
			"test.cases.framework.div.tb14",
			"Contains the manifest headers localized by bundle_en_US.properties test case.",
			"CESAR.ORG", "1.0.1", "http://www.cesar.org.br",
			"info@cesar.org.br", "Should contain the bundle category for tb14",
			"Should contain the bundle copyright for tb14"				};

	private String[]		tb14_manifestHeadersValues_pt_BR			= {
			"test.cases.framework.div.tb14",
			"Contains the manifest headers localized by bundle_pt_BR.properties test case.",
			"CESAR.ORG", "1.0.1", "http://www.cesar.org.br",
			"info@cesar.org.br", "Should contain the bundle category for tb14",
			"Should contain the bundle copyright for tb14"				};

	private String[]		tb14_manifestHeadersValues_es_ES			= {
			"test.cases.framework.div.tb23",
			"Contains the manifest headers localized by bundle_es_ES.properties test case.",
			"CESAR.ORG", "1.0.1", "http://www.cesar.org.br",
			"info@cesar.org.br", "Should contain the bundle category for tb23",
			"Should contain the bundle copyright for tb23"				};

	private String[]		tb14_manifestHeadersValues_missingLocale	= {
			"bundlename", "bundledescription", "bundlevendor", "1.0.1",
			"docurl", "contactinfo", "bundlecategory", "bundlecopyright"};

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
		testGetHeaders013();
		Locale.setDefault(defaultLocale);
	}

	/**
	 * Tests manifest headers localization for a bundle that does not have
	 * locale file.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	void testGetHeaders001() throws Exception {

		Bundle tb1 = _context.installBundle(_tcHome + "tb1.jar");
		tb1.start();
		Dictionary h = tb1.getHeaders();

		for (int i = 0; i < tb1_manifestHeadersValues.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb1_manifestHeadersValues[i]))
				throw new Exception(
						"Exception on testGetHeaders001. Manifest header localization does not match. Expected "
								+ tb1_manifestHeadersValues[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		tb1.stop();
		tb1.uninstall();

	}

	/**
	 * Tests manifest headers localization for a bundle that has the default
	 * locale properties file, as defined by
	 * Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	void testGetHeaders002() throws Exception {

		Bundle tb8 = _context.installBundle(_tcHome + "tb8.jar");
		tb8.start();
		Dictionary h = tb8.getHeaders();

		for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb8_manifestHeadersValues_default[i]))
				throw new Exception(
						"Exception on testGetHeaders002. Manifest header localization does not match. Expected "
								+ tb8_manifestHeadersValues_default[i]
								+ " was " + h.get(manifestHeadersKeys[i]));
		}

		tb8.stop();
		tb8.uninstall();
		// After the bundle has been uninstalled, it should return manifest
		// headers
		// localized for the default locale at the time the bundle was
		// uninstalled.
		h = tb8.getHeaders();

		for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb8_manifestHeadersValues_default[i]))
				throw new Exception(
						"Exception on testGetHeaders002. Manifest header localization does not match. Expected: "
								+ tb8_manifestHeadersValues_default[i]);
		}
	}

	/**
	 * This method tests manifest headers localization for a bundle that has
	 * specific locale files including the default locale.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	void testGetHeaders003() throws Exception {
		// specify default locale
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		tb9.start();
		Dictionary h = tb9.getHeaders();

		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception on testGetHeaders003-1. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_en_US[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		// If the specified locale is null then the locale returned by
		// java.util.Locale.getDefault is used.
		h = tb9.getHeaders(null);
		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception on testGetHeaders003-2. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_en_US[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		// If the specified locale is the empty string, this method
		// will return the raw (unlocalized) manifest headers including any
		// leading ‘%’
		h = tb9.getHeaders("");

		for (int i = 0; i < tb9_manifestHeadersValues_rawHeaders.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_rawHeaders[i]))
				throw new Exception(
						"Exception on testGetHeaders003-3. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_rawHeaders[i]
								+ " was " + h.get(manifestHeadersKeys[i]));
		}

		tb9.stop();
		tb9.uninstall();
		Locale.setDefault(new Locale("fr", "FR"));
		// After the bundle has been uninstalled, it should return manifest
		// headers
		// localized for the default locale at the time the bundle was
		// uninstalled.
		h = tb9.getHeaders();
		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception on testGetHeaders003-4. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_en_US[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include the default locale.
	 * 
	 * @spec Bundle.getHeaders()
	 */
	void testGetHeaders004() throws Exception {
		Locale.setDefault(new Locale("pt", "BR"));
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		tb9.start();
		Dictionary h = tb9.getHeaders();

		for (int i = 0; i < tb9_manifestHeadersValues_missingLocale.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_missingLocale[i]))
				throw new Exception(
						"Exception on testGetHeaders004. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_missingLocale[i]
								+ " was " + h.get(manifestHeadersKeys[i]));
		}

		tb9.stop();
		tb9.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that does not have
	 * locale file.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders005() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb1 = _context.installBundle(_tcHome + "tb1.jar");
		tb1.start();

		Dictionary h = tb1.getHeaders("en_US");
		for (int i = 0; i < tb1_manifestHeadersValues.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb1_manifestHeadersValues[i]))
				throw new Exception(
						"Exception on testGetHeaders005. Manifest header localization does not match. Expected "
								+ tb1_manifestHeadersValues[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		tb1.stop();
		tb1.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has the default
	 * locale file, as defined by
	 * Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders006() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb8 = _context.installBundle(_tcHome + "tb8.jar");
		tb8.start();
		Dictionary h = tb8.getHeaders("en_US");

		for (int i = 0; i < tb8_manifestHeadersValues_default.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb8_manifestHeadersValues_default[i]))
				throw new Exception(
						"Exception on testGetHeaders006. Manifest header localization does not match. Expected "
								+ tb8_manifestHeadersValues_default[i]
								+ " was " + h.get(manifestHeadersKeys[i]));
		}

		tb8.stop();
		tb8.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files including the default locale.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders007() throws Exception {
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		tb9.start();
		Dictionary h = tb9.getHeaders("en");

		for (int i = 0; i < tb9_manifestHeadersValues_en.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en[i]))
				throw new Exception(
						"Exception on testGetHeaders007. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_en[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		tb9.stop();
		tb9.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include locale requested.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders008() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		tb9.start();
		Dictionary h = tb9.getHeaders("pt_BR");

		for (int i = 0; i < tb9_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception on testGetHeaders008. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_en_US[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		tb9.stop();
		tb9.uninstall();
	}

	/**
	 * Tests manifest headers localization for a bundle that has specific locale
	 * files but does not include the default locale.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders009() throws Exception {
		Locale.setDefault(new Locale("pt", "BR"));
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		tb9.start();
		Dictionary h = tb9.getHeaders("en");

		for (int i = 0; i < tb9_manifestHeadersValues_en.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_en[i]))
				throw new Exception(
						"Exception on testGetHeaders009-1. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_en[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		tb9.stop();
		tb9.uninstall();
		Locale.setDefault(new Locale("en", "US"));
		// After the bundle has been uninstalled, it should return manifest
		// headers
		// localized for the default locale at the time the bundle was
		// uninstalled.
		h = tb9.getHeaders("en");
		for (int i = 0; i < tb9_manifestHeadersValues_missingLocale.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_missingLocale[i]))
				throw new Exception(
						"Exception on testGetHeaders009-2. Manifest header localization does not match. Expected: "
								+ tb9_manifestHeadersValues_missingLocale[i]
								+ " was " + h.get(manifestHeadersKeys[i]));
		}
	}

	/**
	 * Tests manifest localization when bundle is not on default location. Tests
	 * manifest localization for a fragment bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders010() throws Exception {
		
		Locale.setDefault(new Locale("pt", "BR"));
		// install host bundle
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		// install fragment bundle
		Bundle tb14 = _context.installBundle(_tcHome + "tb14.jar");
		
		// Start the bundles to force them to resolve.
		tb9.start();
		//tb14.start();

		// When searching for a localization file of a fragment bundle,
		// it must first look in the fragment’s host bundle (with the lowest
		// bundle id) and then look
		// in the host’s currently attached fragment bundles.
		Dictionary h = tb14.getHeaders("pt_BR");

		for (int i = 0; i < tb14_manifestHeadersValues_pt_BR.length; i++) {
			if (!h.get(tb14_manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_pt_BR[i]))
				throw new Exception(
						"Exception on testGetHeaders010-1. Manifest header localization does not match. Expected "
								+ tb14_manifestHeadersValues_pt_BR[i]
								+ " was "
								+ h.get(tb14_manifestHeadersKeys[i]));
		}

		h = tb14.getHeaders("en_US");
		
		// Bug #385 indicates that this was invalid. The code assume
		// the tb9 default was returned because the bundles were not started. However,
		// the framework may resolve fragments at will. If the host/fragment 
		// were resolved, a fragment must consult its host before looking in itself.
		
		// manifest localization before bundle is resolved.
		for (int i = 0; i < tb14_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(tb14_manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception on testGetHeaders010-2. Manifest header localization does not match. Expected "
								+ tb14_manifestHeadersValues_en_US[i]
								+ " was "
								+ h.get(tb14_manifestHeadersKeys[i]));
		}

		tb14.uninstall();
		tb9.uninstall();
	}

	/**
	 * Tests manifest localization for a host bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders011() throws Exception {

		Locale.setDefault(new Locale("en", "US"));
		Bundle tb14 = _context.installBundle(_tcHome + "tb14.jar");
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		tb9.start();
		// When searching for a localization file of a host bundle,
		// it must first look in the bundle and then look in the currently
		// attached fragment bundles.
		Dictionary h = tb9.getHeaders("pt_BR");

		for (int i = 0; i < tb9_manifestHeadersValues_pt_BR.length; i++) {
			if (!h.get(manifestHeadersKeys[i]).equals(
					tb9_manifestHeadersValues_pt_BR[i]))
				throw new Exception(
						"Exception on testGetHeaders011. Manifest header localization does not match. Expected "
								+ tb9_manifestHeadersValues_pt_BR[i]
								+ " was "
								+ h.get(manifestHeadersKeys[i]));
		}

		tb9.stop();
		tb9.uninstall();
		tb14.uninstall();
	}

	/**
	 * Tests manifest localization for a fragment bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders012() throws Exception {

		Locale.setDefault(new Locale("en", "US"));
		// install fragment bundle
		Bundle tb14 = _context.installBundle(_tcHome + "tb14.jar");
		// install host bundle
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");

		// When searching for a localization file of a fragment bundle,
		// it must first look in the fragment’s host bundle (with the lowest
		// bundle id) and then look
		// in the host’s currently attached fragment bundles.

		// resolve fragment bundle
		tb9.start();
		// manifest localization after bundle is resolved.
		Dictionary h = tb14.getHeaders("en_US");
		for (int i = 0; i < tb14_manifestHeadersValues_en_US.length; i++) {
			if (!h.get(tb14_manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_en_US[i]))
				throw new Exception(
						"Exception on testGetHeaders012. Manifest header localization does not match. Expected "
								+ tb14_manifestHeadersValues_en_US[i]
								+ " was "
								+ h.get(tb14_manifestHeadersKeys[i]));
		}
		tb14.uninstall();
		tb9.stop();
		tb9.uninstall();
	}

	/**
	 * Tests manifest localization for a fragment with multiple hosts bundle.
	 * 
	 * @spec Bundle.getHeaders(String)
	 */
	void testGetHeaders013() throws Exception {
		// default locale should exist in any of the following
		// bundles locale file
		Locale.setDefault(new Locale("fr", "CA"));
		Bundle tb14 = _context.installBundle(_tcHome + "tb14.jar");
		Bundle tb23 = _context.installBundle(_tcHome + "tb23.jar");
		Bundle tb9 = _context.installBundle(_tcHome + "tb9.jar");
		Bundle tb9a = _context.installBundle(_tcHome + "tb9a.jar");
		tb9.start();
		tb9a.start();
		// If a fragment is attached to more than one host, the search
		// will only include the first host (that is the host bundle with the
		// lowest bundle id).
		Dictionary h = tb14.getHeaders("fr_FR");

		for (int i = 0; i < tb14_manifestHeadersValues_missingLocale.length; i++) {
			if (!h.get(tb14_manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_missingLocale[i]))
				throw new Exception(
						"Exception on testGetHeaders013-1. Manifest header localization does not match. Expected "
								+ tb14_manifestHeadersValues_missingLocale[i]
								+ " was " + h.get(tb14_manifestHeadersKeys[i]));
		}

		h = tb14.getHeaders("es_ES");

		for (int i = 0; i < tb14_manifestHeadersValues_es_ES.length; i++) {
			if (!h.get(tb14_manifestHeadersKeys[i]).equals(
					tb14_manifestHeadersValues_es_ES[i]))
				throw new Exception(
						"Exception on testGetHeaders013-2. Manifest header localization does not match. Expected "
								+ tb14_manifestHeadersValues_es_ES[i]
								+ " was "
								+ h.get(tb14_manifestHeadersKeys[i]));
		}

		tb9.stop();
		tb9a.stop();
		tb9.uninstall();
		tb9a.uninstall();
		tb14.uninstall();
		tb23.uninstall();
	}

}
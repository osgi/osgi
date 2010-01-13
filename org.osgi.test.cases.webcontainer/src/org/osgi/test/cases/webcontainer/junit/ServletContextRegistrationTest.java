/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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
package org.osgi.test.cases.webcontainer.junit;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.test.cases.webcontainer.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test web container extender registers servletcontext with the web
 *          application bundle with bundle-symbolicname, bundle-version and
 *          web-contextpath information
 */
public class ServletContextRegistrationTest extends
		WebContainerTestBundleControl {

	private final static String OSGI_WEB_SYMBOLICNAME = "osgi.web.symbolicname";
	private final static String OSGI_WEB_VERSION = "osgi.web.version";
	private final static String OSGI_WEB_CONTEXTPATH = "osgi.web.contextpath";

	public void testSimpleServletContextReg001() throws Exception {
		final Map<String, Object> option = createOptions("1.0", "ct-testwar1", "/tw1");
        this.b = super.installWar(option, "tw1.war", true);
	    registerWarBundleTest(option, "tw1.war", true, this.b);
	}

	public void testSimpleServletContextReg004() throws Exception {
		final Map<String, Object> option = createOptions("4.0", "ct-testwar4", "/tw4");
		this.b = super.installWar(option, "tw4.war", false);
		registerWarBundleTest(option, "tw4.war", false, this.b);
	}

	public void testSimpleServletContextReg005() throws Exception {
		final Map<String, Object> option = createOptions("5.0", "ct-testwar5", "/tw5");
		this.b = super.installWar(option, "tw5.war", true);
		registerWarBundleTest(option, "tw5.war", true, this.b);
	}

	public void testSimpleServletContextReg006() throws Exception {
		final Map<String, Object> option = createOptions(null, null, "/tw1");
		this.b = super.installWar(option, "tw1.war", false);
		registerWarBundleTest(option, "tw1.war", false, this.b);
	}

	public void testSimpleServletContextReg009() throws Exception {
		final Map<String, Object> option = createOptions(null, "ct-testwar9", null);
		this.b = super.installWar(option, "tw4.war", true);
		registerWarBundleTest(option, "tw4.war", true, this.b);
	}

	public void testSimpleServletContextReg010() throws Exception {
		final Map<String, Object> option = createOptions("1.0", null, "/tw5");
		this.b = super.installWar(option, "tw5.war", false);
		registerWarBundleTest(option, "tw5.war", false, this.b);
	}

	/*
	 * test deploy a newer version of the same app
	 */
	public void testMultiServletContextReg001() throws Exception {
		final Map<String, Object> option = createOptions(null, null, null);
		this.b = super.installWar(option, "tw1.war", true);
		
		Bundle b2 = super.installWar(option, "tw1.war", true);
		Bundle b3 = super.installWar(option, "tw1.war", true);
		registerWarBundleTest(option, "tw1.war", true, this.b);
		registerWarBundleTest(option, "tw1.war", true, b2);
		registerWarBundleTest(option, "tw1.war", true, b3);
	}

	/*
	 * test deploy a newer version of the same app
	 */
	public void testMultiServletContextReg002() throws Exception {
		final Map<String, Object> option = createOptions(null, "ct-testwar1", null);
		this.b = super.installWar(option, "tw1.war", true);	
		Bundle b2 = super.installWar(option, "tw1.war", true);
		Bundle b3 = super.installWar(option, "tw1.war", true);
		registerWarBundleTest(option, "tw1.war", true, this.b);
		registerWarBundleTest(option, "tw1.war", true, b2);
		registerWarBundleTest(option, "tw1.war", true, b3);
		// uninstallBundle(b2);
		// uninstallBundle(b3);
	}

	/*
	 * test deploy a newer version of the same app
	 */
	public void testMultiServletContextReg003() throws Exception {
		final Map<String, Object> option = createOptions("1.0", null, null);
		final Map<String, Object> option2 = createOptions("1.1", null, null);
		final Map<String, Object> option3 = createOptions("1.2", null, null);
		this.b = super.installWar(option, "tw1.war", true);	
		Bundle b2 = super.installWar(option2, "tw1.war", true);
		Bundle b3 = super.installWar(option3, "tw1.war", true);
		registerWarBundleTest(option, "tw1.war", true, this.b);
		registerWarBundleTest(option2, "tw1.war", true, b2);
		registerWarBundleTest(option3, "tw1.war", true, b3);
		// uninstallBundle(b2);
		// uninstallBundle(b3);
	}

	/*
	 * test 5 web applications. verify bundle-version and bundle-symbolicname
	 * combined can be created uniquely to identify the bundle, when some are
	 * not specified by deploy options.
	 */
	public void testMultiServletContextReg004() throws Exception {
		
		Map<String, Object> option = createOptions("1.0", "ct-testwar1", "/tw1");
		this.b = super.installWar(option, "tw1.war", true);
		registerWarBundleTest(option, "tw1.war", true, this.b);
		
		Bundle[] bundles = new Bundle[4];
		try {
    		option = createOptions("1.0", "ct-wm-testwar1", "/wmtw1");
    		bundles[0] = super.installWar(option, "wmtw1.war", true);
    		registerWarBundleTest(option, "wmtw1.war", true, bundles[0]);
    
    		option = createOptions("1.0", "ct-testwar4", "/tw4");
    		bundles[1] = super.installWar(option,  "tw4.war", true);
    		registerWarBundleTest(option, "tw4.war", true, bundles[1]);
    		
    		option = createOptions("1.0", "ct-wm-testwar", "/wmtw4");
    		bundles[2] = super.installWar(option,  "wmtw4.war", true);
    		registerWarBundleTest(option, "wmtw4.war", true, bundles[2]);
    		

    		option = createOptions("1.0", "ct-testwar5", "/tw5");
    		bundles[3] = super.installWar(option,  "tw5.war", true);
    		registerWarBundleTest(option, "tw5.war", true, bundles[3]);
        } finally {
            for (int i = 0; i < bundles.length; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }

	}

    /*
     * verify install 100 web applications
     */
    public void testMultiServletContextReg005() throws Exception {
        Bundle[] bundles = new Bundle[100];
        final Map<String, Object> option = new HashMap<String, Object>();
        try {
            for (int i = 0; i < 100; i++) {
                bundles[i] = super.installWar(option, "tw1.war", true);
                registerWarBundleTest(option, "tw1.war", true, bundles[i]);
            }
        } finally {
            for (int i = 0; i < 100; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }
    }

	private Map<String, Object> createOptions(String version, String sname, String cp) {
		final Map<String, Object> options = new HashMap<String, Object>();
		options.put(Constants.BUNDLE_VERSION, version);
		options.put(Constants.BUNDLE_SYMBOLICNAME, sname);
		options.put(WEB_CONTEXT_PATH, cp);
		return options;
	}
	
	private void registerWarBundleTest(Map<String, Object> options, String warName,
			boolean start, Bundle bundle) throws Exception {

		String cp = options.get(WEB_CONTEXT_PATH) == null ? null
				: (String) options.get(WEB_CONTEXT_PATH);
		ServiceReference sr = null;
		ServletContext sc;
		
		// validate the bundle
		assertNotNull("Bundle b should not be null", bundle);
		Manifest originalManifest = super.getManifestFromWarName(warName);
		BundleManifestValidator validator = new BundleManifestValidator(bundle,
				originalManifest, options, this.debug);
		validator.validate();

		if (!start) {
			assertEquals("Bundle status should be Resolved but not Active",
					Bundle.RESOLVED, bundle.getState());
			if (cp != null) {
				assertFalse(
						"Bundle not started yet - should not be able to access "
								+ cp, super.ableAccessPath(cp));
			}
			sr = getContext().getServiceReference(
					ServletContext.class.getName());
			assertNull(
					"sr should be null as the bundle has not been started yet",
					sr);
			bundle.start();

		}

		// get the service reference by Bundle-SymbolicName and Bundle-Version
		String filter = "(" + OSGI_WEB_SYMBOLICNAME + "="
				+ (String) bundle.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME)
				+ ")";
		if (debug) {
			log("filter is " + filter);
		}
		ServiceReference[] srs = getContext().getServiceReferences(
				ServletContext.class.getName(), filter);
		assertNotNull(srs);
		for (int i = 0; i < srs.length; i++) {
			// bundle-version is optional
			Object bv1 = bundle.getHeaders().get(Constants.BUNDLE_VERSION);
			Object bv2 = srs[i].getProperty(OSGI_WEB_VERSION);

			if (bv1 == null) {
				if (bv2 == null) {
					sr = srs[i];
				}
			} else {
				if (bv2 != null) {
					if (new Version((String) bv1).compareTo(new Version(
							(String) bv2)) == 0) {
						sr = srs[i];
					}
				}
			}
		}

		assertNotNull(sr);
		sc = (ServletContext) getContext().getService(sr);
		assertEquals("check if servlet context path is correct", sr
				.getProperty(OSGI_WEB_CONTEXTPATH), sc.getContextPath());

		// set the content path now if it was null before
		if (cp == null) {
			cp = sc.getContextPath();
		} else {
			assertEquals(cp, sc.getContextPath());
		}

		if (debug) {
			log("WebContext-Path is " + cp);
		}
		assertEquals(sc, (ServletContext) getContext().getService(sr));

		// get the service reference by context-path
		srs = getContext().getServiceReferences(
				ServletContext.class.getName(),
				"(" + OSGI_WEB_CONTEXTPATH + "="
						+ (String) bundle.getHeaders().get(WEB_CONTEXT_PATH) + ")");
		assertNotNull(srs);
		assertEquals(sc, (ServletContext)getContext().getService(srs[0]));

		// rough test able to access the app
		assertTrue("should be able to access " + cp, super.ableAccessPath(cp));

		assertEquals("Bundle status should be Active", Bundle.ACTIVE, bundle
				.getState());
		try {
			String response = super.getResponse(cp);
			super.checkHomeResponse(response, warName);
		} catch (Exception e) {
			fail("should not be getting an exception here " + e.getMessage());
		}

		bundle.stop();
		// test unable to access pathes yet as it is not started
		assertEquals("Bundle status should be Resolved but not Active",
				Bundle.RESOLVED, bundle.getState());
		assertFalse("Bundle not started yet - should not be able to access "
				+ cp, super.ableAccessPath(cp));

		srs = getContext().getServiceReferences(
				ServletContext.class.getName(),
				"("
						+ OSGI_WEB_SYMBOLICNAME
						+ "="
						+ (String) bundle.getHeaders().get(
								Constants.BUNDLE_SYMBOLICNAME) + ")");
		assertNull("srs should be null as the bundle has been stopped", srs);

		if (start) {
			bundle.start();
		}
	}
}

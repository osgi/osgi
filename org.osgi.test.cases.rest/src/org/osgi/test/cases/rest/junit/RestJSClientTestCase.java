/*
 * Copyright (c) OSGi Alliance (2004, 2015). All Rights Reserved.
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
package org.osgi.test.cases.rest.junit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.osgi.framework.Bundle;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RestJSClientTestCase extends RestTestUtils {
	private static final String	SUCCESS	= "success";
	private String	jsclient;

	public void setUp() throws Exception {
		super.setUp();

		System.out.println("Current directory: " + System.getProperty("user.dir"));
		File restJSClient = new File(System.getProperty("user.dir") + "/../org.osgi.impl.service.rest.client.js/src/rest_client.js");
		jsclient = restJSClient.getCanonicalFile().toURI().toURL().toString();
	}

	public void testFrameworkStartLevelRestClient() throws Exception {
		int sl = getFrameworkStartLevel().getStartLevel();
		int ibsl = getFrameworkStartLevel().getInitialBundleStartLevel();

		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.getFrameworkStartLevel({"
				+ "  success : function(res) {"
				+ "    assert('original startLevel', " + sl + ", res.startLevel);"
				+ "    assert('original initialBundleStartLevel', " + ibsl + ", res.initialBundleStartLevel);"
				+ "    done();"
				+ "  }});");

		// Modify the start level
		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "var fsl = {"
				+ "  startLevel : " + (sl + 1) + ","
				+ "  initialBundleStartLevel : " + (ibsl + 2) + ","
				+ "};"
				+ "client.setFrameworkStartLevel(fsl, {"
				+ "  success : function(res) {"
				+ "    client.getFrameworkStartLevel({"
				+ "      success : function(res) {"
				+ "        assert('updated startLevel', " + (sl + 1) + ", res.startLevel);"
				+ "        assert('updated initialBundleStartLevel', " + (ibsl + 2) + ", res.initialBundleStartLevel);"
				+ "        done();"
				+ "}})}});");

		// Back to original
		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "var fsl = {"
				+ "  startLevel : " + sl + ","
				+ "  initialBundleStartLevel : " + ibsl + ","
				+ "};"
				+ "client.setFrameworkStartLevel(fsl, {"
				+ "  success : function(res) {"
				+ "    client.getFrameworkStartLevel({"
				+ "      success : function(res) {"
				+ "        assert('reverted startLevel', " + sl + ", res.startLevel);"
				+ "        assert('reverted initialBundleStartLevel', " + ibsl + ", res.initialBundleStartLevel);"
				+ "        done();"
				+ "}})}});");

		// Set to invalid value
		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "var fsl = {"
				+ "  startLevel : -1,"
				+ "  initialBundleStartLevel : " + ibsl + ","
				+ "};"
				+ "client.setFrameworkStartLevel(fsl, {"
				+ "  failure : function(res) { "
				+ "    assert('Should have returned Bad Request on negative start level', "
				+ "      400, res);"
				+ "    done(); "
				+ "}});");

		// Check that the original start levels are still current
		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.getFrameworkStartLevel({"
				+ "  success : function(res) {"
				+ "    assert('original startLevel', " + sl + ", res.startLevel);"
				+ "    assert('original initialBundleStartLevel', " + ibsl + ", res.initialBundleStartLevel);"
				+ "    done();"
				+ "}});");
	}

	public void testBundleListRestClient() throws Exception {
		StringBuilder sb = new StringBuilder("[");
		boolean first = true;
		for (Bundle b : getContext().getBundles()) {
			if (first)
				first = false;
			else
				sb.append(",");

			sb.append("'framework/bundle/");
			sb.append(b.getBundleId());
			sb.append("'");
		}
		sb.append("]");

		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.getBundles({"
				+ "  success : function(res) {"
				+ "    assert('getBundles', " + sb.toString() + ", res);"
				+ "    done();"
				+ ""
				+ "}})");

		// TODO filter ?

		Bundle tb1Bundle = getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
		if (tb1Bundle != null) {
			// test bundle is already installed => uninstall
			tb1Bundle.uninstall();
		}
		String url = getContext().getBundle().getEntry(TB1).toString();

		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.installBundle('" + url + "', {"
				+ "  success : function(res) {"
				+ "    assert('installBundle', 'framework/bundle/" + 
				getContext().getBundles().length + "', res);"
				+ "    done();"
				+ ""
				+ "}})");
		tb1Bundle = getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
		assertNotNull("Test bundle TB1 is installed", tb1Bundle);

		assertEquals("Bundle location", "framework/bundle/" + (getContext().getBundles().length - 1),
				getBundleURI(tb1Bundle));

		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.installBundle('" + url + "', {"
				+ "  failure : function(res) { "
				+ "    assert('Should have returned CONFLICT as the bundle is already installed at the location', "
				+ "      409, res);"
				+ "    done(); "
				+ ""
				+ "}})");
		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.installBundle('invalid bundle location', {"
				+ "  failure : function(res) { "
				+ "    assert('Should have returned BAD REQUEST as the bundle location is invalid', "
				+ "      400, res);"
				+ "    done(); "
				+ ""
				+ "}})");
	}

	public void testInstallBundleUpload() throws Exception {
		/*
		 * This should test the post operation on the bundles resource where the
		 * bundle is uploaded as the body of the message.
		 */
		fail("Not yet supported by JS client");
	}

	public void testBundleRepresentationsListRestClient() throws Exception {
		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.getBundleRepresentations({"
				+ "  success : function(res) {"
				+ "    assert('getBundleRepresentations', 'TODO create expected representations', res);"
				+ "    done();"
				+ ""
				+ "}})");
	}

	public void testBundleRestClient() throws Exception {
		Bundle bundle = getRandomBundle();
		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.getBundle('" + bundle.getBundleId() + "', {"
				+ "  success : function(res) {"
				+ "    assert('Bundle ID', " + bundle.getBundleId() + ", res.id);"
				+ "    assert('Bundle Location', '" + bundle.getLocation() + "', res.location);"
				+ "    assert('Symbolic Name', '" + bundle.getSymbolicName() + "', res.symbolicName);"
				+ "    assert('State', " + bundle.getState() + ", res.state);"
				+ "    assert('Last Modified', " + bundle.getLastModified() + ", res.lastModified);"
				+ "    assert('Version', '" + bundle.getVersion() + "', res.version);"
				+ "    done();"
				+ ""
				+ "}})");
	}

	public void jsTest(String script) throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html>"
				+ "<body onload='executeTest()'>"
				+ "<script src=\"" + jsclient + "\"></script>"
                + "<script>"
				+ "/* TODO the console should really come from HTML Unit, why isn't it available? */"
				+ "console = new Object();"
				+ "console.log = function log(arg) {};"
				+ "function assert(msg, expected, actual) {"
				+ "  if (Array.isArray(expected)) {"
				+ "    return assertArray(msg, expected, actual);"
				+ "  } else if (expected !== actual) {"
				+ "    document.getElementById('test_errors').innerHTML += "
				+ "      msg + ': Expected ' + expected + ' but was ' + actual;"
				+ "    return false;"
				+ "  } else {"
				+ "    return true;"
				+ "}}"
				+ "function assertArray(msg, expected, actual) {"
				+ "  if (!assert('Array length different', expected.length, actual.length)) {"
				+ "    return false;"
				+ "  }"
				+ "  for (var i=0; i<expected.length; i++) {"
				+ "    if (!assert('Array element different: ' + i, expected[i], actual[i])) {"
				+ "      return false;"
				+ "    }"
				+ "  }"
				+ "  return true"
				+ "}"
				+ "function done() {"
				+ "  if (document.getElementById('test_errors').innerHTML == '') {"
				+ "    document.getElementById('test_result').innerHTML = '" + SUCCESS + "';"
				+ "  } else {"
				+ "    document.getElementById('test_result').innerHTML = 'error: ' + "
				+ "      document.getElementById('test_errors').innerHTML;"
				+ "}}"
				+ "function executeTest() { "
                + "  var res = testFunction();"
                + "  if (!(res === undefined)) {"
                + "    document.getElementById('test_result').innerHTML = res;"
                + "  }"
				+ "}"
				+ "function testFunction() {");
        html.append(script);
		html.append("}</script>"
                + "<p id='test_result'>undefined</p>"
				+ "<p id='test_errors'></p>"
                + "</body>"
                + "</html>");

        File f = File.createTempFile("jstest-", ".tmp");

        try {
			OutputStream fos = new FileOutputStream(f);
            try {
                fos.write(html.toString().getBytes());
            } finally {
            	fos.close();
            }

            WebClient wc = new WebClient();
            HtmlPage page = wc.getPage(f.toURI().toURL());

            String result = page.getHtmlElementById("test_result").asText();
            int count = 30;
            while (count-- > 0 && "undefined".equals(result)) {
                // Maybe the result arrives asynchronously
                Thread.sleep(500);
                result = page.getHtmlElementById("test_result").asText();
            }
			if (!SUCCESS.equals(result))
				fail(result);
        } finally {
            f.delete();
        }
    }
}
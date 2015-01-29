/*
 * Copyright (c) OSGi Alliance (2004, 2013). All Rights Reserved.
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

	public void testGetFrameworkStartLevel() throws Exception {
		int sl = getFrameworkStartLevel().getStartLevel();
		int ibsl = getFrameworkStartLevel().getInitialBundleStartLevel();

		jsTest("var client = new OsgiRestClient('" + baseURI + "');"
				+ "client.getFrameworkStartLevel({"
				+ "  success : function(res) {"
				+ "    assert('Start Level', " + sl + ", res.startLevel);"
				+ "    assert('Initial Bundle Start Level', " + ibsl + ", res.initialBundleStartLevel);"
				+ "    done();"
				+ "  }});");
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
				+ "  if (expected !== actual) {"
				+ "    document.getElementById('test_errors').innerHTML += "
				+ "      msg + ': Expected ' + expected + ' but was ' + actual;"
				+ "}}"
				+ "function done() {"
				+ "  if (document.getElementById('test_errors').innerHTML == '') {"
				+ "    document.getElementById('test_result').innerHTML = 'success';"
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
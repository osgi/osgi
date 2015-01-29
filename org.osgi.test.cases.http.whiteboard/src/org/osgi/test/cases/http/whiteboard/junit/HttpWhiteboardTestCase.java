/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
package org.osgi.test.cases.http.whiteboard.junit;

import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.runtime.dto.RuntimeDTO;
import org.osgi.service.http.runtime.dto.ServletContextDTO;

public class HttpWhiteboardTestCase extends BaseHttpWhiteboardTestCase {

	@Override
	protected String[] getBundlePaths() {
		return new String[] {"/tb1.jar", "/tb2.jar"};
	}

	public void test_basicServlet() throws Exception {
		Assert.assertEquals("a", request("TestServlet1"));
	}

	public void test_servletInContext() throws Exception {
		Assert.assertEquals("/sc1", request("/sc1/TestServlet2"));
	}

	// public void test_Servlet5() throws Exception {
	// String expected = "Equinox Jetty-based Http Service";
	// String actual = request("TestServlet5");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet6() throws Exception {
	// String expected = "a";
	// String actual = request("something/a.TestServlet6");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet7() throws Exception {
	// String expected = "a";
	// String actual = request("TestServlet7/a");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet8() throws Exception {
	// String expected = "Equinox Jetty-based Http Service";
	// String actual = request("TestServlet8");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet9() throws Exception {
	// String expected = "Equinox Jetty-based Http Service";
	// String actual = request("TestServlet9");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet10() throws Exception {
	// String expected = "a";
	// String actual = request("TestServlet10");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Servlet11() throws Exception {
	// String expected = "a";
	// String actual = request("TestServlet11");
	// Assert.assertEquals(expected, actual);
	// }
	//

	public void test_ErrorPage1() throws Exception {
		Map<String, List<String>> response = request("TestErrorPage1/a", null);
		String responseCode = response.get("responseCode").get(0);
		String actual = response.get("responseBody").get(0);

		Assert.assertEquals("403", responseCode);
		Assert.assertEquals("403 ERROR", actual);
	}

	public void test_ErrorPage2() throws Exception {
		Map<String, List<String>> response = request("TestErrorPage2/a", null);
		String responseCode = response.get("responseCode").get(0);
		String actual = response.get("responseBody").get(0);

		Assert.assertEquals("500", responseCode);
		Assert.assertEquals("500 ERROR", actual);
	}

	public void test_basicFilter() throws Exception {
		Assert.assertEquals("bab", request("TestFilter1/bab"));
	}

	public void test_twoFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcacb", request("TestFilter2/bcacb"));
	}

	public void test_threeFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcdadcb", request("TestFilter3/bcdadcb"));
	}

	public void test_threeFiltersOrderedByRanking() throws Exception {
		Assert.assertEquals("dbcacbd", request("TestFilter4/dbcacbd"));
	}

	public void test_basicExtensionFilter() throws Exception {
		Assert.assertEquals("bab", request("something/bab.TestFilter5"));
	}

	public void test_twoExtensionFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcacb", request("something/bcacb.TestFilter6"));
	}

	public void test_threeExtensionFiltersOrderedByRegistration() throws Exception {
		Assert.assertEquals("bcdadcb", request("something/bcdadcb.TestFilter7"));
	}

	public void test_threeExtensionFiltersOrderedByRanking() throws Exception {
		Assert.assertEquals("dbcacbd", request("something/dbcacbd.TestFilter8"));
	}

	// public void test_Filter9() throws Exception {
	// String expected = "bab";
	// String actual = request("TestFilter9/bab");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter10() throws Exception {
	// String expected = "cbabc";
	// String actual = request("TestFilter10/cbabc");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter11() throws Exception {
	// String expected = "cbdadbc";
	// String actual = request("TestFilter11/cbdadbc");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter12() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("TestFilter12/dcbabcd");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter13() throws Exception {
	// String expected = "bab";
	// String actual = request("something/a.TestFilter13");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter14() throws Exception {
	// String expected = "cbabc";
	// String actual = request("something/a.TestFilter14");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter15() throws Exception {
	// String expected = "cbdadbc";
	// String actual = request("something/a.TestFilter15");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Filter16() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("something/a.TestFilter16");
	// Assert.assertEquals(expected, actual);
	// }

	public void test_Registration11() throws Exception {
		BundleContext bundleContext = getContext();
		ServiceReference<HttpServiceRuntime> serviceReference =
				bundleContext.getServiceReference(HttpServiceRuntime.class);

		Assert.assertNotNull(serviceReference);

		HttpServiceRuntime runtime = bundleContext.getService(serviceReference);
		RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
		ServletContextDTO[] servletContextDTOs = runtimeDTO.servletContextDTOs;

		Assert.assertTrue(servletContextDTOs.length > 0);
	}

	public void test_resourceDTO() throws Exception {
		BundleContext bundleContext = getContext();
		ServiceReference<HttpServiceRuntime> serviceReference =
				bundleContext.getServiceReference(HttpServiceRuntime.class);

		Assert.assertNotNull(serviceReference);

		HttpServiceRuntime runtime = bundleContext.getService(serviceReference);
		RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
		ServletContextDTO[] servletContextDTOs = runtimeDTO.servletContextDTOs;

		ServletContextDTO servletContextDTO = null;

		for (ServletContextDTO curServletContextDTO : servletContextDTOs) {
			if (curServletContextDTO.name.equals("default")) {
				servletContextDTO = curServletContextDTO;
			}
		}

		Assert.assertNotNull(servletContextDTO);

		ResourceDTO resourceDTO = servletContextDTO.resourceDTOs[0];

		Assert.assertEquals("/TestResource1/*", resourceDTO.patterns[0]);
		Assert.assertEquals("/org/osgi/test/cases/http/whiteboard/tb1/resources", resourceDTO.prefix);
	}

	public void test_resourceMatchByRegexMatch() throws Exception {
		Assert.assertEquals("a", request("TestResource1/resource1.txt"));
	}

	public void test_resourceMatchByExactMatch() throws Exception {
		Assert.assertEquals("a", request("TestResource2/a"));
	}

	// public void test_Resource3() throws Exception {
	// String expected = "a";
	// String actual = request("TestResource3/resource1.txt");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Resource4() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("TestResource4/resource1.txt");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Resource5_1() throws Exception {
	// String expected = "dcbabcd";
	// String actual = request("TestResource4/resource1.txt");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_Runtime() throws Exception {
	// BundleContext bundleContext = getContext();
	// ServiceReference<HttpServiceRuntime> serviceReference =
	// bundleContext.getServiceReference(HttpServiceRuntime.class);
	// Assert.assertNotNull(serviceReference);
	//
	// if (serviceReference == null) {
	// return;
	// }
	//
	// HttpServiceRuntime runtime = bundleContext.getService(serviceReference);
	// Assert.assertNotNull(runtime);
	// RuntimeDTO runtimeDTO = runtime.getRuntimeDTO();
	// ServletContextDTO[] servletContextDTOs = runtimeDTO.servletContextDTOs;
	// Assert.assertTrue(servletContextDTOs.length > 0);
	// ServletContextDTO servletContextDTO = servletContextDTOs[0];
	// Assert.assertNotNull(servletContextDTO.contextName);
	// }

	// public void test_ServletContext1() throws Exception {
	// String expected =
	// "/org/eclipse/equinox/http/servlet/tests/tb1/resource1.txt";
	// String actual = request("TestServletContext1");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_ServletContextHelper10() throws Exception {
	// String expected = "cac";
	// String actual = request("a/TestServletContextHelper10/a");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_WBServlet1() throws Exception {
	// String expected = "a";
	// String actual = request("WBServlet1/a");
	// Assert.assertEquals(expected, actual);
	// }
	//
	// public void test_WBServlet2() throws Exception {
	// String expected = "bab";
	// String actual = request("WBServlet2/a");
	// Assert.assertEquals(expected, actual);
	// }

}

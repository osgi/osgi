package org.osgi.test.cases.http.whiteboard.junit;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.servlet.Servlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedResourceDTO;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockServlet;

public class ResourceTestCase extends BaseHttpWhiteboardTestCase {

	public void test_table_140_6_HTTP_WHITEBOARD_RESOURCE_validation() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, 34l);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/http/whiteboard/junit/res");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedResourceDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, 45);
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedResourceDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, new String[] {"/a", "/b"});
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedResourceDTO.failureReason);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, new String[] {"/a", "/b"});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/http/whiteboard/junit/res/index.txt");
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(failedResourceDTO);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(resourceDTO);
		assertEquals(2, resourceDTO.patterns.length);
	}

	public void test_table_140_6_HTTP_WHITEBOARD_RESOURCE_required() {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(failedResourceDTO);
		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(resourceDTO);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(failedResourceDTO);
		resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(resourceDTO);
	}

	public void test_140_6() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/http/whiteboard/junit/res");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(resourceDTO);
		assertEquals("a", request("index.txt"));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases");
		sr.setProperties(properties);

		resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(resourceDTO);
		assertEquals("404", request("index.txt", null).get("responseCode").get(0));
		assertEquals("a", request("http/whiteboard/junit/res/index.txt"));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/res/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/http/whiteboard/junit/res");
		sr.setProperties(properties);

		resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(resourceDTO);
		assertEquals("404", request("http/whiteboard/junit/res/index.txt", null).get("responseCode").get(0));
		assertEquals("a", request("res/index.txt"));
	}

	public void test_140_6_20to21() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/other.txt");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/http/whiteboard/junit/res/index.txt");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(resourceDTO);
		assertEquals("a", request("other.txt"));
		assertEquals("404", request("index.txt", null).get("responseCode").get(0));
		assertEquals("404", request("org/osgi/test/cases/http/whiteboard/junit/res/index.txt", null).get("responseCode").get(0));
	}

	public void test_140_6_20to21_commonProperties() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/other.txt");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/http/whiteboard/junit/res/index.txt");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=foo)");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_NO_SERVLET_CONTEXT_MATCHING, failedResourceDTO.failureReason);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(resourceDTO);
		assertEquals("404", request("other.txt", null).get("responseCode").get(0));

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, "(some=foo)");
		sr.setProperties(properties);

		failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(failedResourceDTO);

		resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(resourceDTO);
		assertEquals("404", request("other.txt", null).get("responseCode").get(0));
	}

	public void test_140_6_1() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("b"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/org/osgi/test/cases/http/whiteboard/junit/res");
		ServiceRegistration<Object> sr = context.registerService(Object.class, new Object(), properties);
		serviceRegistrations.add(sr);

		FailedResourceDTO failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(failedResourceDTO);
		assertEquals(DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE, failedResourceDTO.failureReason);

		ResourceDTO resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(resourceDTO);
		assertEquals("b", request("index.txt"));

		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		sr.setProperties(properties);

		resourceDTO = getResourceDTOByServiceId(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				(Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNotNull(resourceDTO);
		assertEquals("a", request("index.txt"));

		failedResourceDTO = getFailedResourceDTOByServiceId((Long) sr.getReference().getProperty(Constants.SERVICE_ID));
		assertNull(failedResourceDTO);
	}

}

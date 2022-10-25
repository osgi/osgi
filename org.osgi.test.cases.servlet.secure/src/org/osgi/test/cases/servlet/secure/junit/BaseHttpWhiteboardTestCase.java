/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.servlet.secure.junit;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.servlet.runtime.HttpServiceRuntime;
import org.osgi.service.servlet.runtime.dto.ErrorPageDTO;
import org.osgi.service.servlet.runtime.dto.FailedErrorPageDTO;
import org.osgi.service.servlet.runtime.dto.FailedFilterDTO;
import org.osgi.service.servlet.runtime.dto.FailedListenerDTO;
import org.osgi.service.servlet.runtime.dto.FailedResourceDTO;
import org.osgi.service.servlet.runtime.dto.FailedServletContextDTO;
import org.osgi.service.servlet.runtime.dto.FailedServletDTO;
import org.osgi.service.servlet.runtime.dto.FilterDTO;
import org.osgi.service.servlet.runtime.dto.ListenerDTO;
import org.osgi.service.servlet.runtime.dto.RequestInfoDTO;
import org.osgi.service.servlet.runtime.dto.ResourceDTO;
import org.osgi.service.servlet.runtime.dto.ServletContextDTO;
import org.osgi.service.servlet.runtime.dto.ServletDTO;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class BaseHttpWhiteboardTestCase extends OSGiTestCase {

	private ServiceTracker<HttpServiceRuntime,ServiceReference<HttpServiceRuntime>>	runtimeTracker;

	public static final String	DEFAULT	= HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME;

	protected RequestInfoDTO calculateRequestInfoDTO(String string) {
		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		return httpServiceRuntime.calculateRequestInfoDTO(string);
	}

	protected String drainInputStream(InputStream is) throws IOException {
		byte[] bytes = new byte[1024];

		StringBuffer buffer = new StringBuffer(1024);

		int length;
		while ((length = is.read(bytes)) != -1) {
			String chunk = new String(bytes, 0, length);
			buffer.append(chunk);
		}
		return buffer.toString().trim();
	}

	protected String[] getBundlePaths() {
		return new String[0];
	}

	protected ErrorPageDTO getErrorPageDTOByName(String context, String name) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(context);

		if (servletContextDTO == null) {
			return null;
		}

		for (ErrorPageDTO errorPageDTO : servletContextDTO.errorPageDTOs) {
			if (name.equals(errorPageDTO.name)) {
				return errorPageDTO;
			}
		}

		return null;
	}

	protected FailedErrorPageDTO getFailedErrorPageDTOByName(String name) {
		for (FailedErrorPageDTO failedErrorPageDTO : getFailedErrorPageDTOs()) {
			if (name.equals(failedErrorPageDTO.name)) {
				return failedErrorPageDTO;
			}
		}

		return null;
	}

	protected FailedErrorPageDTO getFailedErrorPageDTOByServiceId(long serviceId) {
		for (FailedErrorPageDTO failedErrorPageDTO : getFailedErrorPageDTOs()) {
			if (serviceId == failedErrorPageDTO.serviceId) {
				return failedErrorPageDTO;
			}
		}

		return null;
	}

	protected FailedErrorPageDTO[] getFailedErrorPageDTOs() {
		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		return httpServiceRuntime.getRuntimeDTO().failedErrorPageDTOs;
	}

	protected FailedFilterDTO getFailedFilterDTOByName(String name) {
		for (FailedFilterDTO failedFilterDTO : getFailedFilterDTOs()) {
			if (name.equals(failedFilterDTO.name)) {
				return failedFilterDTO;
			}
		}

		return null;
	}

	protected FailedFilterDTO getFailedFilterDTOByServiceId(long serviceId) {
		for (FailedFilterDTO failedFilterDTO : getFailedFilterDTOs()) {
			if (serviceId == failedFilterDTO.serviceId) {
				return failedFilterDTO;
			}
		}

		return null;
	}

	protected FailedFilterDTO[] getFailedFilterDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().failedFilterDTOs;
	}

	protected FailedListenerDTO getFailedListenerDTOByServiceId(long serviceId) {
		for (FailedListenerDTO failedListenerDTO : getFailedListenerDTOs()) {
			if (serviceId == failedListenerDTO.serviceId) {
				return failedListenerDTO;
			}
		}

		return null;
	}

	protected FailedListenerDTO[] getFailedListenerDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().failedListenerDTOs;
	}

	protected FailedResourceDTO getFailedResourceDTOByServiceId(long serviceId) {
		for (FailedResourceDTO failedResourceDTO : getFailedResourceDTOs()) {
			if (serviceId == failedResourceDTO.serviceId) {
				return failedResourceDTO;
			}
		}

		return null;
	}

	protected FailedResourceDTO[] getFailedResourceDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().failedResourceDTOs;
	}

	protected FailedServletContextDTO getFailedServletContextDTOByName(String name) {
		for (FailedServletContextDTO failedServletContextDTO : getFailedServletContextDTOs()) {
			if (name.equals(failedServletContextDTO.name)) {
				return failedServletContextDTO;
			}
		}

		return null;
	}

	protected FailedServletContextDTO getFailedServletContextDTOByServiceId(long serviceId) {
		for (FailedServletContextDTO failedServletContextDTO : getFailedServletContextDTOs()) {
			if (serviceId == failedServletContextDTO.serviceId) {
				return failedServletContextDTO;
			}
		}

		return null;
	}

	protected FailedServletContextDTO[] getFailedServletContextDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().failedServletContextDTOs;
	}

	protected FailedServletDTO getFailedServletDTOByName(String name) {
		for (FailedServletDTO failedServletDTO : getFailedServletDTOs()) {
			if (name.equals(failedServletDTO.name)) {
				return failedServletDTO;
			}
		}

		return null;
	}

	protected FailedServletDTO getFailedServletDTOByServiceId(long serviceId) {
		for (FailedServletDTO failedServletDTO : getFailedServletDTOs()) {
			if (serviceId == failedServletDTO.serviceId) {
				return failedServletDTO;
			}
		}

		return null;
	}

	protected FailedServletDTO[] getFailedServletDTOs() {
		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		return httpServiceRuntime.getRuntimeDTO().failedServletDTOs;
	}

	protected FilterDTO getFilterDTOByName(String contextName, String name) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(contextName);

		if (servletContextDTO == null) {
			return null;
		}

		for (FilterDTO filterDTO : servletContextDTO.filterDTOs) {
			if (name.equals(filterDTO.name)) {
				return filterDTO;
			}
		}

		return null;
	}

	protected HttpServiceRuntime getHttpServiceRuntime() {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> serviceReference =
				context.getServiceReference(HttpServiceRuntime.class);

		assertNotNull(serviceReference);

		return context.getService(serviceReference);
	}

	protected ListenerDTO getListenerDTOByServiceId(String contextName, long serviceId) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(contextName);

		if (servletContextDTO == null) {
			return null;
		}

		for (ListenerDTO listenerDTO : servletContextDTO.listenerDTOs) {
			if (serviceId == listenerDTO.serviceId) {
				return listenerDTO;
			}
		}

		return null;
	}

	protected ResourceDTO getResourceDTOByServiceId(String contextName, long serviceId) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(contextName);

		if (servletContextDTO == null) {
			return null;
		}

		for (ResourceDTO resourceDTO : servletContextDTO.resourceDTOs) {
			if (serviceId == resourceDTO.serviceId) {
				return resourceDTO;
			}
		}

		return null;
	}

	protected long getServiceId(ServiceRegistration<?> sr) {
		return (Long) sr.getReference().getProperty(Constants.SERVICE_ID);
	}

	protected ServletContextDTO getServletContextDTOByName(String name) {
		for (ServletContextDTO servletContextDTO : getServletContextDTOs()) {
			if (name.equals(servletContextDTO.name)) {
				return servletContextDTO;
			}
		}

		return null;
	}

	protected ServletContextDTO[] getServletContextDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().servletContextDTOs;
	}

	protected ServletDTO getServletDTOByName(String context, String name) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(context);

		if (servletContextDTO == null) {
			return null;
		}

		for (ServletDTO servletDTO : servletContextDTO.servletDTOs) {
			if (name.equals(servletDTO.name)) {
				return servletDTO;
			}
		}

		return null;
	}

	protected URL getServerURL(String path) throws MalformedURLException {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		return new URL(
				"http", getProperty("org.apache.felix.http.host"),
				getIntegerProperty("org.osgi.service.servlet.port", 8080), path);
	}

	protected String getSymbolicName(ClassLoader classLoader) throws IOException {
		InputStream inputStream = classLoader.getResourceAsStream(
				"META-INF/MANIFEST.MF");

		Manifest manifest = new Manifest(inputStream);

		return manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
	}

	protected void log(String message) {
		System.out.println(message);
	}

	@SuppressWarnings("resource")
	protected String request(String path) throws InterruptedException, IOException {
		URL serverURL = getServerURL(path);

		log("Requesting: " + serverURL.toString()); //$NON-NLS-1$

		HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();

		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(150 * 1000);
		connection.setReadTimeout(150 * 1000);
		connection.connect();

		int responseCode = connection.getResponseCode();

		InputStream stream;

		for (int i = 0; i < 9; i++) {
			if (responseCode >= 400) {
				if (i < 8) {
					connection.connect();

					responseCode = connection.getResponseCode();

					Thread.sleep(100);
				}
				else {
					stream = connection.getErrorStream();
				}
			}
			else {
				stream = connection.getInputStream();
			}
		}

		if (responseCode >= 400) {
			stream = connection.getErrorStream();
		}
		else {
			stream = connection.getInputStream();
		}

		try {
			return drainInputStream(stream);
		} finally {
			stream.close();
		}
	}

	protected Map<String, List<String>> request(String path, Map<String, List<String>> headers) throws InterruptedException, IOException {
		URL serverURL = getServerURL(path);

		log("Requesting: " + serverURL.toString()); //$NON-NLS-1$

		HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();

		if (headers != null) {
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				for (String entryValue : entry.getValue()) {
					connection.setRequestProperty(entry.getKey(), entryValue);
				}
			}
		}

		int responseCode = connection.getResponseCode();

		Map<String, List<String>> map = new HashMap<String, List<String>>(connection.getHeaderFields());
		map.put("responseCode", Collections.singletonList(String.valueOf(responseCode)));

		InputStream stream;

		if (responseCode >= 400) {
			stream = connection.getErrorStream();
		}
		else {
			stream = connection.getInputStream();
		}

		try {
			map.put("responseBody", Arrays.asList(drainInputStream(stream)));

			return map;
		} finally {
			stream.close();
		}
	}

	protected ConditionalPermissionAdmin getPermissionAdmin() {
		BundleContext context = getContext();

		ServiceReference<ConditionalPermissionAdmin> serviceReference = context
				.getServiceReference(ConditionalPermissionAdmin.class);

		assertNotNull(serviceReference);

		return context.getService(serviceReference);
	}

	@Override
	protected void setUp() throws Exception {
		for (String bundlePath : getBundlePaths()) {
			Bundle bundle = install(bundlePath);

			bundle.start();

			bundles.add(bundle);
		}
		this.runtimeTracker = new ServiceTracker<>(getContext(),
				HttpServiceRuntime.class,
				new ServiceTrackerCustomizer<HttpServiceRuntime,ServiceReference<HttpServiceRuntime>>() {

					@Override
					public ServiceReference<HttpServiceRuntime> addingService(
							ServiceReference<HttpServiceRuntime> reference) {
						final Object obj = reference
								.getProperty("service.changecount");
						if (obj != null) {
							httpRuntimeChangeCount
									.set(Long.valueOf(obj.toString()));
						}
						return reference;
					}

					@Override
					public void modifiedService(
							ServiceReference<HttpServiceRuntime> reference,
							ServiceReference<HttpServiceRuntime> service) {
						addingService(reference);
					}

					@Override
					public void removedService(
							ServiceReference<HttpServiceRuntime> reference,
							ServiceReference<HttpServiceRuntime> service) {
						httpRuntimeChangeCount.set(-1);
					}

				});
		this.runtimeTracker.open();
	}

	@Override
	protected void tearDown() throws Exception {
		if (this.runtimeTracker != null) {
			this.runtimeTracker.close();
			this.runtimeTracker = null;
		}
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();
		}

		serviceRegistrations.clear();

		for (Bundle bundle : bundles) {
			bundle.uninstall();
		}

		bundles.clear();
	}

	protected List<Bundle> bundles = new ArrayList<Bundle>();
	protected Set<ServiceRegistration<?>> serviceRegistrations = new HashSet<ServiceRegistration<?>>();

	final AtomicLong						httpRuntimeChangeCount	= new AtomicLong(
			-1);

	protected long getHttpRuntimeChangeCount() {
		return httpRuntimeChangeCount.longValue();
	}

	protected long waitForRegistration(final long previousCount) {
		while (this.httpRuntimeChangeCount.longValue() == previousCount) {
			try {
				Thread.sleep(20L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return this.httpRuntimeChangeCount.longValue();
	}
}

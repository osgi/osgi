package org.osgi.service.rest.client;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface RestClient {

	FrameworkStartLevelDTO getFrameworkStartLevel() throws Exception;

	void setFrameworkStartLevel(FrameworkStartLevelDTO startLevel)
			throws Exception;

	Collection<String> getBundles() throws Exception;

	Collection<BundleDTO> getBundleRepresentations() throws Exception;

	BundleDTO getBundle(long id) throws Exception;

	BundleDTO getBundle(String bundlePath) throws Exception;

	int getBundleState(long id) throws Exception;

	int getBundleState(String bundlePath) throws Exception;

	void startBundle(long id) throws Exception;

	void startBundle(String bundlePath) throws Exception;

	void startBundle(long id, int options) throws Exception;

	void startBundle(String bundlePath, int options) throws Exception;

	void stopBundle(long id) throws Exception;

	void stopBundle(String bundlePath) throws Exception;

	void stopBundle(long id, int options) throws Exception;

	void stopBundle(String bundlePath, int options) throws Exception;

	Map<String, Object> getBundleHeaders(long id) throws Exception;

	Map<String, Object> getBundleHeaders(String bundlePath) throws Exception;

	BundleStartLevelDTO getBundleStartLevel(long id) throws Exception;

	BundleStartLevelDTO getBundleStartLevel(String bundlePath) throws Exception;

	void setBundleStartLevel(long id, BundleStartLevelDTO startLevel)
			throws Exception;

	void setBundleStartLevel(String bundlePath, BundleStartLevelDTO startLevel)
			throws Exception;

	String installBundle(String url) throws Exception;

	String installBundle(String location, InputStream in) throws Exception;

	void uninstallBundle(long id) throws Exception;

	void uninstallBundle(String bundlePath) throws Exception;

	void updateBundle(long id) throws Exception;

	void updateBundle(long id, String url) throws Exception;

	void updateBundle(long id, InputStream in) throws Exception;

	Collection<String> getServices() throws Exception;

	Collection<String> getServices(String filter) throws Exception;

	Collection<ServiceReferenceDTO> getServiceRepresentations()
			throws Exception;

	Collection<ServiceReferenceDTO> getServiceRepresentations(String filter)
			throws Exception;

	ServiceReferenceDTO getServiceReference(long id) throws Exception;

	ServiceReferenceDTO getServiceReference(String servicePath)
			throws Exception;

}
package org.osgi.test.cases.subsystem.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osgi.framework.BundleContext;
import org.osgi.service.subsystem.SubsystemConstants;

public class SubsystemBuilder {
	private final Collection<String> bundles = new HashSet<String>();
	private final BundleContext context;
	private final Map<String, String> deploymentHeaders = new HashMap<String, String>();
	private final Map<String, String> headers = new HashMap<String, String>();
	private final Map<String, InputStream> subsystems = new HashMap<String, InputStream>();
	private final Map<String, Map<String, String>> translations = new HashMap<String, Map<String, String>>();

	public SubsystemBuilder() {
		this(null);
	}

	public SubsystemBuilder(BundleContext context) {
		this.context = context;
	}

	public InputStream build() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		zos.putNextEntry(new ZipEntry("OSGI-INF/SUBSYSTEM.MF"));
		zos.write(buildManifest());
		zos.closeEntry();
		if (!deploymentHeaders.isEmpty()) {
			zos.putNextEntry(new ZipEntry("OSGI-INF/DEPLOYMENT.MF"));
			zos.write(buildDeploymentManifest());
			zos.closeEntry();
		}
		buildTranslations(zos);
		buildBundles(zos);
		buildSubsystems(zos);
		zos.close();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public SubsystemBuilder bundle(String name) {
		bundles.add(name);
		return this;
	}
	
	public SubsystemBuilder deploymentHeader(String name, String value) {
		deploymentHeaders.put(name,  value);
		return this;
	}

	public SubsystemBuilder header(String name, String value) {
		headers.put(name, value);
		return this;
	}

	public SubsystemBuilder subsystem(String name, InputStream content) {
		subsystems.put(name, content);
		return this;
	}

	public SubsystemBuilder translation(String file, String key, String value) {
		if (!file.endsWith(".properties"))
			file += ".properties";
		Map<String, String> m = translations.get(file);
		if (m == null) {
			m = new HashMap<String, String>();
			translations.put(file, m);
		}
		m.put(key, value);
		return this;
	}

	private byte[] buildBundle(String name) throws IOException {
		URL entry = context.getBundle().getEntry(name);
		InputStream is = entry.openStream();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] bytes = new byte[1024];
				int read;
				while ((read = is.read(bytes)) != -1) {
					baos.write(bytes, 0, read);
				}
				return baos.toByteArray();
			} finally {
				baos.close();
			}
		} finally {
			is.close();
		}
	}

	private void buildBundles(ZipOutputStream zos) throws IOException {
		for (String bundle : bundles) {
			ZipEntry entry = new ZipEntry(bundle);
			zos.putNextEntry(entry);
			zos.write(buildBundle(bundle));
			zos.closeEntry();
		}
	}
	
	private byte[] buildDeploymentManifest() throws IOException {
		ManifestBuilder builder = new ManifestBuilder();
		for (Entry<String, String> entry : deploymentHeaders.entrySet()) {
			builder.header(entry.getKey(), entry.getValue());
		}
		return builder.build();
	}

	private byte[] buildManifest() throws IOException {
		ManifestBuilder builder = new ManifestBuilder();
		for (Entry<String, String> entry : headers.entrySet()) {
			builder.header(entry.getKey(), entry.getValue());
		}
		return builder.build();
	}

	private byte[] buildSubsystem(InputStream content) throws IOException {
		InputStream is = content;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] bytes = new byte[1024];
				int read;
				while ((read = is.read(bytes)) != -1) {
					baos.write(bytes, 0, read);
				}
				return baos.toByteArray();
			} finally {
				baos.close();
			}
		} finally {
			is.close();
		}
	}

	private void buildSubsystems(ZipOutputStream zos) throws IOException {
		for (Map.Entry<String, InputStream> entry : subsystems.entrySet()) {
			ZipEntry ze = new ZipEntry(entry.getKey());
			zos.putNextEntry(ze);
			zos.write(buildSubsystem(entry.getValue()));
			zos.closeEntry();
		}
	}

	private byte[] buildTranslation(Map<String, String> properties)
			throws IOException {
		Properties props = new Properties();
		for (Entry<String, String> entry : properties.entrySet())
			props.setProperty(entry.getKey(), entry.getValue());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		props.store(baos, null);
		baos.close();
		return baos.toByteArray();
	}

	private void buildTranslations(ZipOutputStream zos) throws IOException {
		String localization = getSubsystemLocalization();
		String[] tokens = localization.split("/");
		String next = "";
		for (int i = 0; i < tokens.length - 1; i++) {
			next += tokens[i] + '/';
			zos.putNextEntry(new ZipEntry(next));
		}
		int index = localization.lastIndexOf('/');
		String prefix = index == -1 ? "" : localization.substring(0, index);
		for (Entry<String, Map<String, String>> entry : translations.entrySet()) {
			String name = prefix + '/' + entry.getKey();
			zos.putNextEntry(new ZipEntry(name));
			zos.write(buildTranslation(entry.getValue()));
			zos.closeEntry();
		}
	}

	private String getSubsystemLocalization() {
		String value = headers.get(SubsystemConstants.SUBSYSTEM_LOCALIZATION);
		if (value == null)
			value = SubsystemConstants.SUBSYSTEM_LOCALIZATION_DEFAULT_BASENAME;
		return value;
	}
}

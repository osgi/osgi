package nursery.obr.bindex;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import nursery.obr.resource.*;

import org.osgi.service.obr.Requirement;

/**
 * Convert a bundle to a generic resource description and store its local
 * dependencies (like for example a license file in the JAR) in a zip file.
 * 
 * @version $Revision$
 */
public class BundleInfo {
	Manifest	manifest;
	File		zipFile;
	ZipFile		zip;
	String		license;
	Properties	localization;

	/**
	 * Parse a zipFile from the file system. We only need the manifest and the
	 * localization. So a zip file is used to minimze memory consumption.
	 * 
	 * @param zipFile Path name
	 * @throws Exception Any errors that occur
	 */
	public BundleInfo(String zipFile) throws Exception {
		this.zipFile = new File(zipFile);

		if (!this.zipFile.exists())
			throw new FileNotFoundException(zipFile.toString());

	}

	/**
	 * Convert the bundle to a Resource. All URIs are going to be abslute, but
	 * could be local.
	 * 
	 * @return the resource
	 * @throws Exception
	 */
	public ResourceImpl build() throws Exception {
		zip = new ZipFile(zipFile);
		try {
			// Setup the manifest
			// and create a resource
			ZipEntry entry = zip.getEntry("META-INF/MANIFEST.MF");
			if (entry == null)
				throw new FileNotFoundException("No Manifest in "
						+ zipFile.toString());
			manifest = new Manifest(zip.getInputStream(entry));
			ResourceImpl resource = new ResourceImpl(
					manifest.getSymbolicName(), manifest.getVersion());

			// Calculate the location URL of the JAR
			URL location = new URL("jar:" + zipFile.toURL().toString() + "!/");
			resource.setURI(zipFile.toURI());

			// Handle license. -l allows a global license
			// set when no license is included.
			String license = translated("Bundle-License)");
			if (license != null)
				resource.setLicense(new URL(location, license).toURI());
			else if (this.license != null)
				resource.setLicense(new URI(this.license));

			String description = translated("Bundle-Description");
			if (description != null)
				resource.setDescription(description);

			String copyright = translated("Bundle-Copyright");
			if (copyright != null)
				resource.setCopyright(copyright);

			String documentation = translated("Bundle-DocURL");
			if (documentation != null)
				resource.setDocumentation(new URL(location, documentation)
						.toURI());

			String source = manifest.getValue("Bundle-Source");
			if (source != null)
				resource.setSource(new URL(location, source).toURI());

			long size = zipFile.length();
			if (size > 0) {
				resource.setSize(size);
			}
			for (int i = 0; i < manifest.getCategories().length; i++) {
				String category = manifest.getCategories()[i];
				resource.addCategory(category);
			}

			doFragment(resource);
			doRequires(resource);

			resource.addCapability(doBundle());

			List capabilities = doExports();
			for (Iterator i = capabilities.iterator(); i.hasNext();)
				resource.addCapability((CapabilityImpl) i.next());

			for (Iterator i = doImports().iterator(); i.hasNext();)
				resource.addRequirement((RequirementImpl) i.next());

			resource.addRequirement(doExecutionEnvironment());
			return resource;
		}
		finally {
			try {
				zip.close();
			}
			catch (Exception e) {
			}
		}
	}

	private String translated(String key) {
		return translate(manifest.getValue(key));
	}

	void doFragment(ResourceImpl resource) {
		// Check if we are a fragment
		Entry entry = manifest.getHost();
		if (entry == null) {
			return;
		}
		else {
			// We are a fragment, create a requirement
			// to our host.
			RequirementImpl r = new RequirementImpl("bundle");
			StringBuffer sb = new StringBuffer();
			sb.append("(&(symbolicname=");
			sb.append(entry.getName());
			sb.append(")(version>=");
			sb.append(entry.getVersion());
			sb.append("))");
			r.setFilter(sb.toString());
			r.setComment("Required host for Fragment");
			resource.addRequirement(r);

			// We are a fragment, this means we can extend
			// our host. So created an extend to the host
			resource.addExtend(r);

			// And insert a capability that we are available
			// as a fragment. ### Do we need that with extend?
			CapabilityImpl capability = new CapabilityImpl("fragment");
			capability.addProperty("host", entry.getName());
			capability.addProperty("version", entry.getVersion());
			resource.addCapability(capability);
		}
	}

	void doRequires(ResourceImpl resource) {
		List entries = manifest.getRequire();
		if (entries == null)
			return;

		for (Iterator i = entries.iterator(); i.hasNext();) {
			Entry entry = (Entry) i.next();
			RequirementImpl r = new RequirementImpl("bundle");

			StringBuffer sb = new StringBuffer();
			sb.append("(&(symbolicname=");
			sb.append(entry.getName());
			sb.append(")(version>=");
			sb.append(entry.getVersion());
			sb.append("))");
			r.setFilter(sb.toString());
			r.setComment("Require-Bundle " + entry.getName() + "-"
					+ entry.getVersion());
			if (entry.directives == null
					|| "true".equalsIgnoreCase((String) entry.directives
							.get("resolution")))
				r.setCardinality(Requirement.UNARY);
			else
				r.setCardinality(Requirement.OPTIONAL);
			resource.addRequirement(r);
		}
	}

	RequirementImpl doExecutionEnvironment() {
		String[] parts = manifest.getRequiredExecutionEnvironments();
		if (parts == null)
			return null;

		StringBuffer sb = new StringBuffer();
		sb.append("(|");
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			sb.append("(ee=");
			sb.append(part);
			sb.append(")");
		}
		sb.append(")");

		RequirementImpl req = new RequirementImpl("ee");
		req.setFilter(sb.toString());
		req.setComment("Execution Environment");
		return req;
	}

	List doImports() {
		List requirements = new ArrayList();
		List packages = manifest.getImports();
		if (packages == null)
			return requirements;

		for (Iterator i = packages.iterator(); i.hasNext();) {
			Entry pack = (Entry) i.next();
			RequirementImpl requirement = new RequirementImpl("package");
			StringBuffer filter = new StringBuffer();
			filter.append("(&(package=");
			filter.append(pack.getName());
			filter.append(")");
			if (pack.getVersion() != null) {
				filter.append("(version>=");
				filter.append(pack.getVersion());
				filter.append(")");
			}
			Map attributes = pack.getAttributes();
			doAttributes(filter, attributes);
			filter.append(")");
			requirement.setFilter(filter.toString());
			requirement.setComment("Import package " + pack);
			requirements.add(requirement);
		}
		return requirements;
	}

	private void doAttributes(StringBuffer filter, Map attributes) {
		if (attributes != null)
			for (Iterator i = attributes.keySet().iterator(); i.hasNext();) {
				String attribute = (String) i.next();
				if (attribute.equalsIgnoreCase("specification-version")
						|| attribute.equalsIgnoreCase("version"))
					continue;
				else {
					filter.append("(");
					filter.append(attribute);
					filter.append("=");
					filter.append(attributes.get(attribute));
					filter.append(")");
				}
			}
	}

	CapabilityImpl doBundle() {
		CapabilityImpl capability = new CapabilityImpl("bundle");
		capability.addProperty("symbolicname", manifest.getSymbolicName());
		capability.addProperty("version", manifest.getVersion());
		capability
				.addProperty("manifestversion", manifest.getManifestVersion());

		Entry host = manifest.getHost();
		if (host != null) {
			capability.addProperty("host", host.getName());
			if (host.getVersion() != null)
				capability.addProperty("version", host.getVersion());
		}
		return capability;
	}

	private List doExports() {
		List capabilities = new ArrayList();
		List packages = manifest.getExports();
		if (packages != null)
			for (Iterator i = packages.iterator(); i.hasNext();) {
				Entry pack = (Entry) i.next();
				CapabilityImpl capability = new CapabilityImpl("package");
				capabilities.add(capability);
				capability.addProperty("package", pack.getName());
				capability.addProperty("version", pack.getVersion());
				Map attributes = pack.getAttributes();
				if (attributes != null)
					for (Iterator at = attributes.keySet().iterator(); at
							.hasNext();) {
						String key = (String) at.next();
						if (key.equalsIgnoreCase("specification-version")
								|| key.equalsIgnoreCase("version"))
							continue;
						else {
							Object value = attributes.get(key);
							capability.addProperty(key, value);
						}
					}
			}
		return capabilities;
	}

	public String translate(String s) {
		if (s == null)
			return null;

		if (!s.startsWith("%"))
			return s;

		if (localization == null)
			try {
				localization = new Properties();
				String path = manifest
						.getValue("Bundle-Localization", "bundle");
				path += ".properties";
				InputStream in = zip.getInputStream(new ZipEntry(path));
				if (in != null) {
					localization.load(in);
				}
				in.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		return localization.getProperty(s, s.substring(1));
	}
}

package osgi.nursery.resource;

import java.net.URI;
import java.util.*;

import org.osgi.framework.Version;
import org.xmlpull.v1.XmlPullParser;

import osgi.nursery.service.obr.*;
import aQute.lib.tag.Tag;

public class ResourceImpl implements Resource {
	List<CapabilityImpl>	capabilities	= new ArrayList<CapabilityImpl>();
	List<RequirementImpl>	requirements	= new ArrayList<RequirementImpl>();
	List<RequirementImpl>	requests		= new ArrayList<RequirementImpl>();
	List<RequirementImpl>	extensions		= new ArrayList<RequirementImpl>();
	URI						license;
	URI						url;
	URI						documentation;
	String					name;
	Version					version;
	String					description;
	List<String>			categories		= new ArrayList<String>();
	String					copyright;
	URI						source;
	long					size			= -1;

	public ResourceImpl(String name, Version version) {
		this.version = version;
		this.name = name;
	}

	public ResourceImpl(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "resource");
		name = parser.getAttributeValue(null, "name");
		String v = parser.getAttributeValue(null, "version");
		if (v == null)
			version = new Version("0");
		else
			version = new Version(v);
		documentation = toURI(parser.getAttributeValue(null, "documentation"));
		source = toURI(parser.getAttributeValue(null, "source"));
		license = toURI(parser.getAttributeValue(null, "license"));
		url = toURI(parser.getAttributeValue(null, "url"));
		size = toInteger(parser.getAttributeValue(null, "size"));

		while (parser.nextTag() == XmlPullParser.START_TAG) {
			if (parser.getName().equals("description")) {
				description = parser.nextText().trim();
			}
			else if (parser.getName().equals("category")) {
				categories.add(parser.getAttributeValue(null, "id").trim());
			}
			else if (parser.getName().equals("require"))
				addRequirement(new RequirementImpl(parser));
			else if (parser.getName().equals("extend"))
				addExtend(new RequirementImpl(parser));
			else if (parser.getName().equals("request"))
				addRequest(new RequirementImpl(parser));
			else if (parser.getName().equals("capability"))
				addCapability(new CapabilityImpl(parser));
			else if (parser.getName().equals("copyright")) {
				setCopyright(parser.nextText().trim());
			}
			parser.next();
		}
		parser.require(XmlPullParser.END_TAG, null, "resource");
	}

	public void addRequest(RequirementImpl request) {
		if (request != null)
			requests.add(request);
	}

	public void addExtend(RequirementImpl extend) {
		if (extend != null)
			extensions.add(extend);
	}

	private int toInteger(String value) {
		if (value == null)
			return -1;
		else
			return Integer.parseInt(value);
	}

	private URI toURI(String attributeValue) throws Exception {
		if (attributeValue == null)
			return null;

		return new URI(attributeValue);
	}

	public void addCategory(String category) {
		categories.add(category);
	}

	public void addCapability(CapabilityImpl capability) {
		if (capability != null)
			capabilities.add(capability);
	}

	public void addRequirement(RequirementImpl requirement) {
		if (requirement != null)
			requirements.add(requirement);
	}

	public void setLicense(URI license) {
		this.license = license;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Capability[] getCapabilities() {
		return capabilities.toArray(new Capability[capabilities.size()]);
	}
	
	public URI getLicense() {
		return license;
	}

	public String getName() {
		return name;
	}

	public Requirement[] getRequirements() {
		return requirements.toArray(new Requirement[requirements.size()]);
	}
	
	public Requirement[] getRequests() {
		return requests.toArray(new Requirement[requests.size()]);
	}

	public Requirement[] getExtends() {
		return extensions.toArray(new Requirement[extensions.size()]);
	}

	public Tag toXML() {
		Tag meta = new Tag("resource");
		meta.addAttribute("url", url.toString());
		meta.addAttribute("name", getName());
		meta.addAttribute("version", getVersion().toString());

		String description = getDescription();
		if (description != null)
			meta.addContent(new Tag("description", description));

		if (getDocumentation() != null)
			meta.addAttribute("documentation", getDocumentation().toString());

		if (getCopyright() != null)
			meta.addContent(new Tag("copyright", getCopyright()));

		if (getLicense() != null)
			meta.addAttribute("license", getLicense().toString());

		if (size > 0)
			meta.addAttribute("size", String.valueOf(size));

		for (String category : categories) {
			meta.addContent(new Tag("category", new String[] {"id",
					category.toLowerCase()}));
		}

		for (CapabilityImpl capability : capabilities) {
			Tag cap = capability.toXML();
			meta.addContent(cap);
		}
		for (RequirementImpl requirement : requirements) {
			Tag rq = requirement.toXML("require");
			meta.addContent(rq);
		}
		for (RequirementImpl extend : extensions) {
			Tag cap = extend.toXML("extend");
			meta.addContent(cap);
		}
		return meta;
	}

	public URI getURI() {
		return url;
	}

	public void setURI(URI url) {
		this.url = url;
	}

	public String getCopyright() {
		return copyright;
	}

	public Version getVersion() {
		if (version == null)
			version = new Version("0");
		return version;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public URI getDocumentation() {
		return documentation;
	}

	public void setDocumentation(URI documentation) {
		this.documentation = documentation;
	}

	public URI getSource() {
		return source;
	}

	public void setSource(URI source) {
		this.source = source;
	}

	public boolean isSatisfiedBy(RequirementImpl requirement) {
		for (CapabilityImpl capability : capabilities) {
			if (requirement.isSatisfied(capability))
				return true;
		}
		return false;
	}

	public Map asMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (description != null)
			map.put("description", description);
		if (categories != null)
			map.put("category", categories);
		if (copyright != null)
			map.put("copyright", copyright);
		if (license != null)
			map.put("license", license.toString());
		if (source != null)
			map.put("source", source.toString());
		map.put("name", name);
		map.put("version", version);
		map.put("size", size);
		return map;
	}

	public String toString() {
		return name + "-" + version;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Collection<RequirementImpl> getRequirementList() {
		return requirements;
	}

	public Collection<RequirementImpl> getExtendList() {
		return extensions;
	}

	public Collection<RequirementImpl> getRequestList() {
		return requests;
	}

	public Collection<CapabilityImpl> getCapabilityList() {
		return capabilities;
	}

	public int hashCode() { return name.hashCode() ^ version.hashCode(); }
	public boolean equals( Object o ) {
		try {
			ResourceImpl other = (ResourceImpl) o;
			return name.equals(other.name) && version.equals(other.version);
		} catch( ClassCastException e ) {
			return false;
		}
	}
}

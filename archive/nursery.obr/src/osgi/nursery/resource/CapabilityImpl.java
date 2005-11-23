package osgi.nursery.resource;

import java.util.*;

import org.osgi.framework.Version;
import org.xmlpull.v1.XmlPullParser;

import osgi.nursery.service.obr.Capability;

import aQute.lib.tag.Tag;

public class CapabilityImpl implements Capability {
	String				name;
	Map<String, List<Object>>	properties	= new TreeMap<String, List<Object>>();

	public CapabilityImpl(String name) {
		this.name = name;
	}


	public CapabilityImpl(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "capability");
		name = parser.getAttributeValue(null,"name");
		while ( parser.nextTag() == XmlPullParser.START_TAG ) {
			if ( parser.getName().equals("p")) {
				String name = parser.getAttributeValue(null,"n");
				String value = parser.getAttributeValue(null,"v");
				String type = parser.getAttributeValue(null,"t");
				Object v = value;

				if ( "nummeric".equals(type))
					v = new Long(value);
				else if ( "version".equals(type))
					v = new Version(value);
				addProperty(name,v);
			}
			parser.next();
			parser.require(XmlPullParser.END_TAG, null, "p" );
		}
		parser.require(XmlPullParser.END_TAG, null, "capability" );
	}


	public void addProperty(String key, Object value) {
		List<Object> values = properties.get(key);
		if (values == null) {
			values = new ArrayList<Object>();
			properties.put(key, values);
		}
		values.add(value);
	}

	public Tag toXML() {
		Tag tag = new Tag("capability");
		tag.addAttribute("name", name);
		for (String key : properties.keySet()) {
			List values = properties.get(key);
			for (Object value : values) {
				Tag p = new Tag("p");
				tag.addContent(p);
				p.addAttribute("n", key);
				if ( value != null )
					p.addAttribute("v", value.toString());
				else
					System.out.println("Missing value " + key);
				String type = null;
				if (value instanceof Number )
					type = "number";
				else if (value.getClass() == Version.class)
					type = "version";
				if (type != null)
					p.addAttribute("t", type);
			}
		}
		return tag;
	}


	public String getName() {
		return name;
	}


	public Map getProperties() {
		return properties;
	}

}

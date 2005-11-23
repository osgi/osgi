package osgi.nursery.resource;

import org.xmlpull.v1.XmlPullParser;

import osgi.nursery.service.obr.Requirement;

import aQute.lib.tag.Tag;

/**
 * Implements the Requirement interface.
 * 
 * 
 * @version $Revision$
 */
public class RequirementImpl implements Requirement {
	int		id;
	String	name;
	String	filter;
	Filter	_filter;

	/**
	 * Create a requirement with the given name.
	 * 
	 * @param name
	 */
	public RequirementImpl(String name) {
		this.name = name;
	}


	/**
	 * Parse the requirement from the pull parser.
	 * 
	 * @param parser
	 * @throws Exception
	 */
	public RequirementImpl(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "require");
		name = parser.getAttributeValue(null, "name");
		filter = parser.getAttributeValue(null, "filter");
		parser.next();
		parser.require(XmlPullParser.END_TAG, null, "require");
	}

	public void setFilter(String filter) {
		this.filter = filter;
		_filter = null;
	}

	public String getFilter() {
		return filter;
	}

	public Tag toXML() {
		Tag req = new Tag(getTagName());
		req.addAttribute("name", getName());
		req.addAttribute("filter", filter);
		return req;
	}

	protected String getTagName() {
		return "require";
	}

	public String getName() {
		return name;
	}

	public boolean isSatisfied(CapabilityImpl capability) {
		if (_filter == null)
			_filter = new Filter(filter);

		boolean result = _filter.match(capability.properties);
		return result;
	}

	public String toString() {
		return name + " " + filter;
	}
}

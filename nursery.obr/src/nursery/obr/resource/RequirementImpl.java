package nursery.obr.resource;

import org.osgi.service.obr.Requirement;
import org.xmlpull.v1.XmlPullParser;


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
	String	comment;
	int	cardinality = UNARY;
	
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
		parser.require(XmlPullParser.START_TAG, null, null );
		name = parser.getAttributeValue(null, "name");
		filter = parser.getAttributeValue(null, "filter");
		String c = parser.getAttributeValue(null,"cardinality");
		cardinality = UNARY;
		if ( c != null ) {
			if ( "OPTIONAL".equalsIgnoreCase(c))
				cardinality = OPTIONAL;
			else if ( "MULTIPLE".equalsIgnoreCase(c))
				cardinality = MULTIPLE;
			else if ( "UNARY".equalsIgnoreCase(c))
				cardinality = UNARY;
		}
		
		StringBuffer sb = new StringBuffer();
		while ( parser.next() == XmlPullParser.TEXT ) {
			sb.append( parser.getText() );
		}
		if ( sb.length() > 0 )
			setComment(sb.toString().trim());
			
		parser.require(XmlPullParser.END_TAG, null, null );
	}

	public void setFilter(String filter) {
		this.filter = filter;
		_filter = null;
	}

	public String getFilter() {
		return filter;
	}

	public Tag toXML(String name) {
		Tag tag = toXML(this);
		tag.rename(name);
		return tag;
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


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment=comment;
	}


	public int getCardinality() {
		return cardinality;
	}


	public void setCardinality(int value) {
		cardinality = value;
	}


	public static Tag toXML(Requirement requirement) {
		Tag req = new Tag("require");
		req.addAttribute("name", requirement.getName());
		req.addAttribute("filter", requirement.getFilter());
		
		String c = null;
		switch(requirement.getCardinality()) {
			case OPTIONAL: c = "OPTIONAL"; break ;
			case UNARY: c = "UNARY"; break;
			case MULTIPLE: c = "MULTIPLE"; break; 
		}
		req.addAttribute("cardinality", c);
		if ( requirement.getComment() != null )
			req.addContent(requirement.getComment());
		return req;
	}


}

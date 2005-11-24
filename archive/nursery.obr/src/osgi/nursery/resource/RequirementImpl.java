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
		Tag req = new Tag(name);
		req.addAttribute("name", getName());
		req.addAttribute("filter", filter);
		
		String c = null;
		switch(cardinality) {
			case OPTIONAL: c = "OPTIONAL"; break ;
			case UNARY: c = "UNARY"; break;
			case MULTIPLE: c = "MULTIPLE"; break; 
		}
		req.addAttribute("cardinality", c);
		if ( comment != null )
			req.addContent(comment);
		return req;
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


}

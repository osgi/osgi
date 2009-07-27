/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) OSGi 2001 
 */
package org.osgi.test.script;

import java.io.StringReader;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles the low level parsing details.
 */
class ScriptParser extends DefaultHandler {
	SAXParser	parser;
	Tag			current;

	/**
	 * Parse from a url string );
	 */
	public Tag parseURI(String uri) throws Exception {
		return parse(new InputSource(uri));
	}

	/**
	 * Parse from an InputSource.
	 */
	Tag parse(InputSource source) throws Exception {
		current = new Tag("");
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		parser = parserFactory.newSAXParser();
		parser.parse(source, this);
		if (current.getContents().size() != 1)
			throw new RuntimeException(
					"Only 1 top level tag allowed (should be <script>)");
		return (Tag) current.getContents().elementAt(0);
	}

	/**
	 * Parse from a string.
	 */
	public Tag parseString(String string) throws Exception {
		return parse(new InputSource(new StringReader(string)));
	}

	/**
	 * Needed for the ContentHandler interface.
	 */
	public void startDocument() {
	}

	/**
	 * Needed for the ContentHandler interface.
	 */
	public void endDocument() {
	}

	/**
	 * Starts a new element. Create a new tag and push the old.
	 */
	public void startElement(String namespace, String local, String qname,
			Attributes atts) {
		Tag tag = new Tag(local);
		current.addContent(tag);
		tag.parent = current;
		current = tag;
		for (int i = 0; i < atts.getLength(); i++) {
			String name = atts.getLocalName(i);
			String value = atts.getValue(i);
			current.addAttribute(name, value);
		}
	}

	/**
	 * Commit the new tag and pop.
	 */
	public void endElement(String namespace, String local, String qname) {
		current = current.parent;
	}

	/**
	 * Handle text parts.
	 */
	public void characters(char ch[], int start, int length) {
		if (ch != null)
			current.addContent(new String(ch, start, length));
	}

	public void ignorableWhitespace(char ch[], int start, int length) {
	}

	public void processingInstruction(String target, String data) {
	}

	public void endPrefixMapping(String x) {
	}

	public void setDocumentLocator(Locator x) {
	}

	public void skippedEntity(String x) {
	}

	public void startPrefixMapping(String x, String y) {
	}
}

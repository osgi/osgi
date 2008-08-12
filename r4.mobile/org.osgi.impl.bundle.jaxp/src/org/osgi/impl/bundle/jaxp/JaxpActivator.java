package org.osgi.impl.bundle.jaxp;

import java.util.Hashtable;

import javax.xml.parsers.*;

import org.osgi.framework.*;
import org.osgi.util.xml.XMLParserActivator;

public class JaxpActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			if (f != null) {
				f.setNamespaceAware(true);
				f.setValidating(true);
				Hashtable properties = new Hashtable();
				properties.put(XMLParserActivator.PARSER_NAMESPACEAWARE, new Boolean(f.isNamespaceAware()));
				properties.put(XMLParserActivator.PARSER_VALIDATING,
						new Boolean(f.isValidating()));
				context.registerService(DocumentBuilderFactory.class.getName(),
						f, properties);
			}
			else
				System.err.println("No DocumentBuilderFactory");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SAXParserFactory s = SAXParserFactory.newInstance();
			if (s != null) {
				s.setNamespaceAware(true);
				s.setValidating(true);
				Hashtable properties = new Hashtable();
				properties.put(XMLParserActivator.PARSER_NAMESPACEAWARE, 
						new Boolean(s.isNamespaceAware()));
				properties.put(XMLParserActivator.PARSER_VALIDATING, 
						new Boolean(s.isValidating()));
				context.registerService(SAXParserFactory.class.getName(), s,
						properties);
			}
			else
				System.err.println("No SAXParserFactory");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
	}
}

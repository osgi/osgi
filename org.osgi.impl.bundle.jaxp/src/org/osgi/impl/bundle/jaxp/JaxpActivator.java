package org.osgi.impl.bundle.jaxp;

import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.xml.XMLParserActivator;

public class JaxpActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			if (f != null) {
				f.setNamespaceAware(true);
				f.setValidating(true);
				Hashtable<String,Object> properties = new Hashtable<>();
				properties.put(XMLParserActivator.PARSER_NAMESPACEAWARE, Boolean.valueOf(f.isNamespaceAware()));
				properties.put(XMLParserActivator.PARSER_VALIDATING,
						Boolean.valueOf(f.isValidating()));
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
				Hashtable<String,Object> properties = new Hashtable<>();
				properties.put(XMLParserActivator.PARSER_NAMESPACEAWARE, 
						Boolean.valueOf(s.isNamespaceAware()));
				properties.put(XMLParserActivator.PARSER_VALIDATING, 
						Boolean.valueOf(s.isValidating()));
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

	@Override
	public void stop(BundleContext context) throws Exception {
		// nothing to do
	}
}

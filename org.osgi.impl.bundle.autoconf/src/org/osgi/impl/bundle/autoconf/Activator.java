/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */

package org.osgi.impl.bundle.autoconf;

import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.metatype.MetaTypeService;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		ServiceReference[] refs;
		
		refs = context.getServiceReferences(SAXParserFactory.class.getName(),
				"(&(parser.namespaceAware=true)(parser.validating=true))");
		if (refs==null) { throw new Exception("Cannot get a validating parser"); }
		SAXParserFactory saxParserFactory = (SAXParserFactory) context.getService(refs[0]);
		SAXParser saxp = saxParserFactory.newSAXParser();
		
		ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
		if (ref==null) { throw new Exception("cannot get Configuration Admin"); }
		ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(ref);

		ref = context.getServiceReference(MetaTypeService.class.getName());
		if (ref==null) { throw new Exception("cannot get Meta Type Service"); }
		MetaTypeService metaTypeService = (MetaTypeService) context.getService(ref);

		Autoconf autoconf = new Autoconf(context,configurationAdmin,metaTypeService,saxParserFactory);
		Hashtable d = new Hashtable();
		d.put("processor","AutoconfProcessor"); // TODO this is just an "example" in rfc88 5.2.5
		context.registerService(ResourceProcessor.class.getName(),autoconf,d);
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}

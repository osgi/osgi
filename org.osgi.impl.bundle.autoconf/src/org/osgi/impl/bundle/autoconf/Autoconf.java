/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.bundle.autoconf;

import java.io.InputStream;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.metatype.MetaTypeService;
import org.xml.sax.InputSource;

public class Autoconf implements BundleActivator,ResourceProcessor {
	BundleContext context;
	ConfigurationAdmin configurationAdmin;
	MetaTypeService metaTypeService;
	SAXParser saxp;
	int operation;
	DeploymentPackage deploymentPackage;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		ServiceReference[] refs;
		
		refs = context.getServiceReferences(SAXParserFactory.class.getName(),
				"(&(parser.namespaceAware=true)(parser.validating=true))");
		if (refs==null) { throw new Exception("Cannot get a validating parser"); }
		saxp = (SAXParser) context.getService(refs[0]);
		
		ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
		if (ref==null) { throw new Exception("cannot get Configuration Admin"); }
		configurationAdmin = (ConfigurationAdmin) context.getService(ref);

		ref = context.getServiceReference(MetaTypeService.class.getName());
		if (ref==null) { throw new Exception("cannot get Meta Type Service"); }
		metaTypeService = (MetaTypeService) context.getService(ref);

		Hashtable d = new Hashtable();
		d.put("processor","AutoconfProcessor"); // TODO this is just an "example" in rfc88 5.2.5
		context.registerService(ResourceProcessor.class.getName(),this,d);
	}

	public void stop(BundleContext context) throws Exception {
	}

	public void begin(DeploymentPackage rp, int operation) {
		this.operation = operation;
		deploymentPackage = rp;
	}

	public void complete(boolean commit) {
		throw new IllegalStateException("not implemented yet");
	}

	public void process(String name, InputStream stream) throws Exception {
		InputSource is = new InputSource(stream);
		is.setPublicId(name);
		MetaData m = new MetaData(saxp,is);
		
		// TODO check for consistency
	}

	public void dropped(String name) throws Exception {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
	}

	public void dropped() {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
	}
}

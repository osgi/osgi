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
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.bundle.autoconf.MetaData.AD;
import org.osgi.impl.bundle.autoconf.MetaData.Attribute;
import org.osgi.impl.bundle.autoconf.MetaData.OCD;
import org.osgi.impl.bundle.autoconf.MetaData.Object;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeService;
import org.xml.sax.InputSource;

public class Autoconf implements BundleActivator,ResourceProcessor {
	BundleContext context;
	ConfigurationAdmin configurationAdmin;
	MetaTypeService metaTypeService;
	DeploymentAdmin deploymentAdmin;
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
		
		ref = context.getServiceReference(DeploymentAdmin.class.getName());
		if (ref==null) { throw new Exception("cannot get Deployment Admin"); }
		deploymentAdmin =  (DeploymentAdmin) context.getService(ref);

		Hashtable d = new Hashtable();
		d.put("processor","AutoconfProcessor"); // TODO this is just an "example" in rfc88 5.2.5
		context.registerService(ResourceProcessor.class.getName(),this,d);
	}

	public void stop(BundleContext context) throws Exception {
		deploymentPackage = null;
	}

	public void begin(DeploymentPackage rp, int operation) {
		this.operation = operation;
		deploymentPackage = rp;
	}

	public void complete(boolean commit) {
		if (!commit) throw new IllegalStateException("rollback not implemented yet");
	}

	public void process(String name, InputStream stream) throws Exception {
		InputSource is = new InputSource(stream);
		is.setPublicId(name);
		MetaData m = new MetaData(saxp,is);
		
		for(int i = 0; i < m.designates.length; i++) {
			MetaData.Designate d = m.designates[i];
			MetaData.Object o = d.objects[0]; // There can be only one!

			// unfortunately the bundle name is in symbName-version format
			String bundleName = d.bundle;
			int dash = bundleName.indexOf('-');
			String bundleSymbolicName = bundleName.substring(0,dash);
			String bundleVersion = bundleName.substring(dash+1);
			
			
			if (d.factory) {
				throw new IllegalStateException("factories not supported yet!");
			} else {
				Bundle bundle = searchForBundle(bundleSymbolicName,bundleVersion,true);
				if (bundle==null) {
					// TODO what to do if "optional"
					throw new IllegalArgumentException(
						"the bundle "+bundleSymbolicName+" "+bundleVersion+
						" needs to be configured but is not part of the deployment package");
				}
				MetaData.OCD ocd = getOCD(m,o.ocdref);
				if (ocd==null) {
					// TODO: metatype registry lookup
					throw new IllegalArgumentException("no ocd with name "+o.ocdref+" found");
				}
				Map values = createData(ocd,o);
				String location = bundle.getLocation();

				// write out the new configuration
				Configuration conf = configurationAdmin.getConfiguration(d.pid,location);
				Hashtable newProps = new Hashtable();
				if (d.merge) {
					Dictionary oldProps = conf.getProperties();
					for(Enumeration enum = oldProps.keys();enum.hasMoreElements();) {
						String propName = (String) enum.nextElement();
						java.lang.Object propValue = oldProps.get(propName);
						newProps.put(propName,propValue);
					}
				}
				newProps.putAll(values);
				conf.update(newProps);
			}
		}
	}

	/**
	 * creates a hash table where the names are the attribute names,
	 * the values are the attribute values (eg. arrays, Vectors, or wrapper classes
	 * based on the object class definition)<br/>
	 * Only those attributes will be in the table, that are set in the object. If the
	 * OCD defines something, and there is no actual value to it, then it is not included.
	 * 
	 * @param ocd
	 * @param o
	 */
	private Map createData(OCD ocd, Object o) {
		
		// create hash to look up attribute definitions by id
		HashMap types = new HashMap();
		for(int i=0;i<ocd.ads.length;i++) types.put(ocd.ads[i].id,ocd.ads[i]);
 		
		// unfortunately the attribute values are a little messed up.
		// array values are not explicitly described as such. For example an array
		// with three value is written as three attribute with the same name.
		// So we first create a hashmap with all-vector values, then fix the hashmap
		// based on the attribute definitions
		HashMap values = new HashMap();
		for(int i=0;i<o.attributes.length;i++) {
			Attribute a = o.attributes[i];
			if (!types.containsKey(a.adref)) throw new IllegalArgumentException(
					"attribute \""+a.adref+"\" has no attribute definition in "+ocd.id);
			Vector v = (Vector) values.get(a.adref);
			if (v==null) { v=new Vector();values.put(a.adref,v); }
			v.add(a.content);
		}
		
		// at this point values contains vectors of strings
		// fix them up based on attribute definitions
		for(Iterator entries = values.entrySet().iterator();entries.hasNext();) {
			Map.Entry e = (Map.Entry)entries.next();
			MetaData.AD ad = (AD) types.get(e.getKey());
			Vector vec = (Vector) e.getValue();
			int type = ad.type;
			for(int i=0;i<vec.size();i++) {
				String strValue = (String) vec.get(i);
				java.lang.Object realValue;
				switch(type) {
					case AttributeDefinition.BOOLEAN:
						realValue = new Boolean(strValue);
						break;
					case AttributeDefinition.BYTE:
						realValue = new Byte(strValue);
						break;
					case AttributeDefinition.CHARACTER:
						realValue = new Character(strValue.charAt(0));
						break;
					case AttributeDefinition.DOUBLE:
						realValue = new Double(strValue);
						break;
					case AttributeDefinition.FLOAT:
						realValue = new Float(strValue);
						break;
					case AttributeDefinition.INTEGER:
						realValue = new Integer(strValue);
						break;
					case AttributeDefinition.LONG:
						realValue = new Long(strValue);
						break;
					case AttributeDefinition.SHORT:
						realValue = new Short(strValue);
						break;
					case AttributeDefinition.STRING:
						realValue = strValue;
						break;
					default: // should not happen
						throw new IllegalStateException("no such attribute type "+type);
				}
				vec.set(i,realValue);
			}

			// now do cardinality, eg from a vectory<Type> create either Type, vector<Type>
			// or type[] (primitive type array)
			if (ad.cardinality==0) {
				if (vec.size()!=1) throw new IllegalArgumentException(
						"attribute "+ocd.id+"."+ad.id+" has multiple values, but cardinality is 0");
				e.setValue(vec.get(0));
			} else if (ad.cardinality<0) {
				// check for size constraing
				if (ad.cardinality!=Integer.MIN_VALUE) {
					int maxSize = -ad.cardinality;
					if (vec.size()>maxSize) throw new IllegalArgumentException(
						"attribute vector "+ocd.id+"."+ad.id+" has a maximum size of "+maxSize);
					// nothing to do, it is already a vector
				}
			} else {
				// ad.cardinality>0
				if ((ad.cardinality!=Integer.MAX_VALUE) && vec.size()>ad.cardinality) {
					if (vec.size()>ad.cardinality) throw new IllegalArgumentException(
							"attribute array "+ocd.id+"."+ad.id+" has a maximum size of "+ad.cardinality);
				}
				java.lang.Object primitiveArray;
				switch(ad.type) {
					case AttributeDefinition.BOOLEAN:
						boolean[] ba = new boolean[vec.size()];
						for(int i=0;i<vec.size();i++) ba[i]=((Boolean)vec.elementAt(i)).booleanValue();
						primitiveArray = ba;
						break;
					case AttributeDefinition.BYTE:
						byte[] bya = new byte[vec.size()];
						for(int i=0;i<vec.size();i++) bya[i]=((Byte)vec.elementAt(i)).byteValue();
						primitiveArray = bya;
						break;
					case AttributeDefinition.CHARACTER:
						char[] ca = new char[vec.size()];
						for(int i=0;i<vec.size();i++) ca[i]=((Character)vec.elementAt(i)).charValue();
						primitiveArray = ca;
						break;
					case AttributeDefinition.DOUBLE:
						double[] da = new double[vec.size()];
						for(int i=0;i<vec.size();i++) da[i]=((Double)vec.elementAt(i)).doubleValue();
						primitiveArray = da;
						break;
					case AttributeDefinition.FLOAT:
						float[] fa = new float[vec.size()];
						for(int i=0;i<vec.size();i++) fa[i]=((Float)vec.elementAt(i)).floatValue();
						primitiveArray = fa;
						break;
					case AttributeDefinition.INTEGER:
						int[] ia = new int[vec.size()];
						for(int i=0;i<vec.size();i++) ia[i]=((Integer)vec.elementAt(i)).intValue();
						primitiveArray = ia;
						break;
					case AttributeDefinition.LONG:
						long[] la = new long[vec.size()];
						for(int i=0;i<vec.size();i++) la[i]=((Long)vec.elementAt(i)).longValue();
						primitiveArray = la;
						break;
					case AttributeDefinition.SHORT:
						short[] sa = new short[vec.size()];
						for(int i=0;i<vec.size();i++) sa[i]=((Short)vec.elementAt(i)).shortValue();
						primitiveArray = sa;
						break;
					case AttributeDefinition.STRING:
						String[] stra = new String[vec.size()];
						primitiveArray = vec.toArray(stra);
						break;
					default: // should not happen
						throw new IllegalStateException("no such attribute type "+type);
				}
				e.setValue(primitiveArray);
			}
		}
		return values;
	}

	/**
	 * @param m
	 * @param ocdref
	 * @return
	 */
	private OCD getOCD(MetaData m, String ocdref) {
		for(int i=0;i<m.ocds.length;i++) {
			OCD ocd = m.ocds[i];
			if (ocd.id.equals(ocdref)) return ocd;
		}
		return null;
	}

	/**
	 * search for a bundle with the given symbolic name and version in an array.
	 * @param bundleSymbolicName
	 * @param bundleVersion
	 * @return the bundle that matches, or null, if not found
	 */
	private Bundle searchForBundle(String bundleSymbolicName, String bundleVersion,boolean onlyInDeploymentPackage) {
		Bundle[] bundles;
		if (onlyInDeploymentPackage) {
			bundles = deploymentPackage.listBundles();
		} else {
			throw new IllegalArgumentException("TODO");//TODO
		}
		
		for (int i = 0; i < bundles.length; i++) {
			Bundle b = bundles[i];
			Dictionary h = b.getHeaders();
			if (!bundleSymbolicName.equals(h.get("Bundle-SymbolicName"))) continue;
			if (!bundleVersion.equals(h.get("Bundle-Version"))) continue;
			return b;
		}
		return null;
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

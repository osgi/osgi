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

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.impl.bundle.autoconf.MetaData.AD;
import org.osgi.impl.bundle.autoconf.MetaData.Attribute;
import org.osgi.impl.bundle.autoconf.MetaData.OCD;
import org.osgi.impl.bundle.autoconf.MetaData.Object;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentSession;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeService;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Autoconf implements ResourceProcessor {
	final BundleContext context;
	final ConfigurationAdmin configurationAdmin;
	final MetaTypeService metaTypeService;
	final SAXParserFactory saxParserFactory;
	int operation;
	DeploymentSession	session;

	public Autoconf(
			BundleContext context,
			ConfigurationAdmin configurationAdmin,
			MetaTypeService metaTypeService,
			SAXParserFactory saxParserFactory) {
		this.context = context;
		this.configurationAdmin = configurationAdmin;
		this.metaTypeService = metaTypeService;
		this.saxParserFactory = saxParserFactory;
	}

	public void begin(DeploymentSession session) {
		this.session = session;
	}

	public void process(String name, InputStream stream) throws DeploymentException {
		InputSource is = new InputSource(stream);
		is.setPublicId(name);
		MetaData m;
		try {
			m = new MetaData(saxParserFactory.newSAXParser(),is);
		}
		catch (ParserConfigurationException e) {
			throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,"Cannot create XML parser",e);
		}
		catch (IOException e) {
			throw new DeploymentException(DeploymentException.CODE_MISSING_RESOURCE,"Cannot read configuration",e);
		}
		catch (SAXException e) {
			throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,"Malformed configuration data in "+is.getSystemId(),e);
		}
		
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
				Configuration conf;
				try {
					conf = configurationAdmin.getConfiguration(d.pid,location);
				}
				catch (IOException e) {
					throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,"Cannot get configuration",e);
				}
				Hashtable newProps = new Hashtable();
				if (d.merge) {
					Dictionary oldProps = conf.getProperties();
					for(Enumeration e = oldProps.keys();e.hasMoreElements();) {
						String propName = (String) e.nextElement();
						java.lang.Object propValue = oldProps.get(propName);
						newProps.put(propName,propValue);
					}
				}
				newProps.putAll(values);
				try {
					conf.update(newProps);
				}
				catch (IOException e) {
					throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,"Cannot write configuration",e);
				}
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
		if (onlyInDeploymentPackage) {
			return session.getSourceDeploymentPackage().getBundle(bundleSymbolicName);
		} else {
			throw new IllegalArgumentException("TODO");//TODO
		}
	}

	public void dropped(String name) {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
	}

	public void dropped() {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
	}


	public void dropAllResources() throws DeploymentException {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
		
	}

	public void prepare() throws DeploymentException {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
		
	}

	public void commit() {
		// TODO Auto-generated method stub
	}

	public void rollback() {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
		
	}

	public void cancel() {
		throw new IllegalStateException("not implemented yet");
		// TODO Auto-generated method stub
		
	}
}

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

import java.io.*;
import java.net.URL;
import java.security.*;
import java.util.*;

import javax.xml.parsers.*;

import org.osgi.framework.*;
import org.osgi.impl.bundle.autoconf.MetaData.*;
import org.osgi.impl.bundle.autoconf.MetaData.Object;
import org.osgi.service.cm.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.deploymentadmin.*;
import org.osgi.service.deploymentadmin.spi.*;
import org.osgi.service.metatype.*;
import org.xml.sax.*;

public class Autoconf implements ResourceProcessor {
	BundleContext context;
	ConfigurationAdmin configurationAdmin;
	MetaTypeService metaTypeService;
	SAXParserFactory saxParserFactory;
	int operation;
	LinkedList commitTasks;
	StoredConfigurations storedConfigurations;
	String packageName;
	DeploymentPackage	sourceDeploymentPackage;
	

	private static class CommitTask {
		public Dictionary properties;
		public Configuration configuration;
		public String pidAlias;
		public String factoryPid;
		public String resourceName;
	}

	
	protected void activate(final ComponentContext context) throws PrivilegedActionException {
		configurationAdmin = (ConfigurationAdmin) context.locateService("configurationAdmin");
		metaTypeService = (MetaTypeService) context.locateService("metaTypeService");
		saxParserFactory = (SAXParserFactory) context.locateService("saxParserFactory");
		this.context = context.getBundleContext();
		AccessController.doPrivileged(new PrivilegedExceptionAction(){
			public java.lang.Object run() throws Exception {
				File stf = context.getBundleContext().getDataFile("storedConfigurations");
				storedConfigurations = new StoredConfigurations(stf);
				return null;
			}});
	}
	
	public void begin(DeploymentSession session) {
		packageName = session.getSourceDeploymentPackage().getName();
		if ("".equals(packageName)) packageName = session.getTargetDeploymentPackage().getName();
		sourceDeploymentPackage = session.getSourceDeploymentPackage();
		commitTasks = new LinkedList();
	}

	/**
	 * Process a designate element, and put it 
	 * @param d
	 * @param ocds
	 * @throws DeploymentException if the element cannot be processed for any reason. It 
	 * is the callers' responsibility to recover work if the element is optional
	 */
	public CommitTask processDesignate(MetaData.Designate d,MetaData.OCD[] ocds) throws DeploymentException {
		MetaData.Object o = d.objects[0]; // There can be only one!

		// unfortunately the bundle name is in symbName-version format
		final Bundle bundle = searchForBundle(d.bundle,d.factoryPid==null);

		if (bundle==null) {
			throw new IllegalArgumentException(
				"the bundle "+d.bundle+
				" needs to be configured but is not part of the deployment package");
		}

		MetaData.OCD ocd = getOCD(ocds,o.ocdref);
		ObjectClassDefinition realOCD=null;
		if (ocd==null) {
			MetaTypeInformation mti = metaTypeService.getMetaTypeInformation(bundle);
			if (mti==null) {
				throw new IllegalArgumentException("no ocd found for pid "+d.pid);
			}
			if (d.factoryPid!=null) {
				realOCD = mti.getObjectClassDefinition(d.factoryPid,null);
			} else {
				realOCD = mti.getObjectClassDefinition(d.pid,null);
			}
			if (realOCD==null) {
				throw new IllegalArgumentException("no ocd found for pid "+d.pid);
			}
		}

		Map values = createData(ocd,realOCD,o);
		String location = (String) AccessController.doPrivileged(new PrivilegedAction(){
			public java.lang.Object run() {
				return bundle.getLocation();
			}});
		
		if (d.factoryPid!=null) {
			CommitTask toWrite = new CommitTask();
			try {
				final String fp = d.factoryPid;
				final String l = location;
				toWrite.configuration = (Configuration) AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public java.lang.Object run() throws Exception {
						return configurationAdmin.createFactoryConfiguration(fp,l);
					}});
			} catch (PrivilegedActionException e) {
				Exception cause = e.getException();
				if (cause instanceof IOException) {
					throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,"Cannot create new factory configuration",e);
				}  else {
					throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,e.getClass().getName()+":"+e.getMessage());
				}
			}

			toWrite.properties = new Hashtable(values);
			toWrite.factoryPid = d.factoryPid;
			toWrite.pidAlias = d.pid;
			return toWrite;
		} else {
			CommitTask toWrite = new CommitTask();
			try {
				toWrite.configuration = configurationAdmin.getConfiguration(d.pid,location);
			}
			catch (IOException e) {
				throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,"Cannot get configuration",e);
			}

			if (d.merge) {
				Dictionary oldProps = toWrite.configuration.getProperties();
				Hashtable newProps = new Hashtable();
				for(Enumeration e = oldProps.keys();e.hasMoreElements();) {
					String propName = (String) e.nextElement();
					java.lang.Object propValue = oldProps.get(propName);
					newProps.put(propName,propValue);
				}
				newProps.putAll(values);
				toWrite.properties = newProps;
			} else {
				toWrite.properties = new Hashtable(values);
			}
			return toWrite;
		}
	}
	
	public void process(final String resourceName, final InputStream stream) throws ResourceProcessorException {

		// this contains those configurations that were created by the same package, and the same
		// resource in some previous version
		List oldConfigs = storedConfigurations.getByPackageAndResourceName(packageName,resourceName);

		List newConfigs = new LinkedList();

		InputSource is = new InputSource(stream);
		is.setPublicId(resourceName);
		MetaData m;
		try {
			saxParserFactory.setValidating(true);
			saxParserFactory.setNamespaceAware(true);
			SAXParser sp = saxParserFactory.newSAXParser();
			
			try {
				sp.setProperty(MetaData.JAXP_SCHEMA_LANGUAGE,MetaData.W3C_XML_SCHEMA);
				URL autoconfURL = MetaData.class.getResource("autoconf.xsd");
				sp.setProperty(MetaData.JAXP_SCHEMA_SOURCE,autoconfURL.toExternalForm());
			} catch (SAXNotRecognizedException e) {
				// ok, if the parser doesn't do schemas, don't validate
				System.err.println("Warning: no schema-capable xml parser available, autoconf file won't be validated");
				saxParserFactory.setValidating(false);
				sp = saxParserFactory.newSAXParser();
			}
			m = new MetaData(sp,is);
		}
		catch (ParserConfigurationException e) {
			throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR,"Cannot create XML parser",e);
		}
		catch (IOException e) {
			throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR,"Cannot read configuration",e);
		}
		catch (SAXException e) {
			throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR,"Malformed configuration data in "+is.getSystemId(),e);
		}
		catch (PrivilegedActionException e) {
			throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR,"",e);
		}
		
		designates:
		for(int i = 0; i < m.designates.length; i++) {
			MetaData.Designate d = m.designates[i];
			try {
				CommitTask toWrite = processDesignate(d,m.ocds);
				toWrite.resourceName = resourceName;
				commitTasks.add(toWrite);
				newConfigs.add(new StoredConfiguration(packageName,resourceName,toWrite.configuration.getPid(),toWrite.factoryPid,toWrite.pidAlias));
			} catch (DeploymentException e) {
				if (d.optional) {
					// TODO should log something
					continue; 
				}
				throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR,"Deployment Exception",e);
			}
		}
		
		// delete all configurations that are not in the new one
		oldConfigs.removeAll(newConfigs);
		for (Iterator iter = oldConfigs.iterator(); iter.hasNext();) {
			StoredConfiguration sc = (StoredConfiguration) iter.next();
			CommitTask toWrite = new CommitTask();
			try {
				final String p = sc.pid;
				toWrite.configuration = (Configuration) AccessController.doPrivileged(new PrivilegedExceptionAction(){
					public java.lang.Object run() throws Exception {
						return configurationAdmin.getConfiguration(p);
					}}); 
			}
			catch (PrivilegedActionException e) {
				Exception cause = e.getException();
				throw new ResourceProcessorException(ResourceProcessorException.CODE_OTHER_ERROR,
						"Configuration with pid "+sc.pid+" needs to be removed, but cannot be "+
						"accessed",cause);
			}
			toWrite.properties = null;
			commitTasks.add(toWrite);
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
	 * @param realOCD 
	 * @param o
	 */
	private Map createData(OCD ocd, ObjectClassDefinition realOCD, Object o) {
		
		// create hash to look up attribute definitions by id
		HashMap types = new HashMap();
		if (ocd!=null) {
			for(int i=0;i<ocd.ads.length;i++) types.put(ocd.ads[i].id,ocd.ads[i]);
		} else {
			AttributeDefinition[] ads = realOCD.getAttributeDefinitions(ObjectClassDefinition.ALL);
			for (int i = 0; i < ads.length; i++) {
				AttributeDefinition rad = ads[i];
				types.put(rad.getID(),new MetaData.AD(rad));
			}
		}
 		
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
	 * Looks up the object class definition in the metadata file.
	 * @param ocds array of the object classes in the metadata
	 * @param ocdref
	 * @return
	 */
	private OCD getOCD(MetaData.OCD[] ocds, String ocdref) {
		for(int i=0;i<ocds.length;i++) {
			OCD ocd = ocds[i];
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
	private Bundle searchForBundle(String bundleLocation,boolean onlyInDeploymentPackage) {
		if (onlyInDeploymentPackage) {
			if (!bundleLocation.startsWith("osgi-dp:")) throw new IllegalArgumentException(
					"the bundle at location \""+bundleLocation+
					"\" needs to be configured but the location string doesn't start with osgi-dp:");
			final String bundleSymbolicName = bundleLocation.substring(8);
			return (Bundle) AccessController.doPrivileged(new PrivilegedAction() {
				public java.lang.Object run() {
					return sourceDeploymentPackage.getBundle(bundleSymbolicName);
				}});
		} else {
			// linear search is "fast enough" here
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				final Bundle b = bundles[i];
				String l = (String) AccessController.doPrivileged(new PrivilegedAction() {
					public java.lang.Object run() {
						return b.getLocation();
					}});
				if (bundleLocation.equals(l)) return b;
			}
			return null;
		}
	}

	public void dropped(String name) {
		// TODO Auto-generated method stub
	}


	public void dropAllResources() throws ResourceProcessorException {
		List toDrop = storedConfigurations.getByPackageName(packageName);
		for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
			StoredConfiguration sc = (StoredConfiguration) iter.next();
			CommitTask ct = new CommitTask();
			try {
				final String p = sc.pid;
				ct.configuration = (Configuration) AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public java.lang.Object run() throws Exception {
						return configurationAdmin.getConfiguration(p);
					}});
				ct.properties = null;
				commitTasks.add(ct);
			}
			catch (PrivilegedActionException e) {
				// TODO what to do here? forceful/non-forceful things?
				e.printStackTrace();
			}
		}
	}

	public void prepare() throws ResourceProcessorException {
		// everything is prepared in the 'process' call
	}

	public void commit() {
		try {
			for (Iterator i = commitTasks.iterator(); i.hasNext();) {
				CommitTask oc = (CommitTask) i.next();
				String pid = oc.configuration.getPid();
				try {
					if (oc.properties==null) {
						oc.configuration.delete();
						storedConfigurations.remove(pid);
					} else {
						oc.configuration.update(oc.properties);
						StoredConfiguration sc = new StoredConfiguration(
								packageName,
								oc.resourceName,
								pid,
								oc.factoryPid,
								oc.pidAlias);
						storedConfigurations.add(sc);
					}
				}
				catch (IOException e) {
					// TODO what to do when the commit fails because of access control violation?
					// TODO the process() method should somehow filter this out, but there is only one
					// way currently: call the update() method there. But this is something the
					// spec forbids
					e.printStackTrace();
				}
			}
		} finally {
			flushStoredConfigurations();
		}
	} 

	/**
	 * write out storedConfiguration to the persistent storage.
	 */
	private void flushStoredConfigurations() {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public java.lang.Object run() throws Exception {
					storedConfigurations.flush();
					return null;
				}});
		}
		catch (PrivilegedActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rollback() {
		// we didn't do a thing, so simply leave everything as it is
	}

	public void cancel() {
		// there's not much to do. This resource processor doesn't do any computing intensive
		// thing, and doesn't access slow resources.
	}
}

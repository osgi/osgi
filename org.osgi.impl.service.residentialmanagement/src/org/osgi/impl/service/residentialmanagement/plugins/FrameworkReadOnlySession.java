/*
 * Copyright (c) OSGi Alliance (2000-2011).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.residentialmanagement.plugins;

import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Date;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.DmtConstants;

/**
 * 
 * @author Shigekuni KONDO, NTT Corporation
 */
class FrameworkReadOnlySession implements ReadableDataSession, SynchronousBundleListener{
	
	protected static final String FRAMEWORKSTARTLEVEL = "StartLevel";
	protected static final String INITIALBUNDLESTARTLEVEL = "InitialBundleStartLevel";
	protected static final String PROPERTY = "Property";
	
	protected static final String BUNDLE = "Bundle";
	protected static final String URL = "URL";
	protected static final String AUTOSTART = "AutoStart";
	protected static final String FAULTTYPE = "FaultType";
	protected static final String FAULTMESSAGE = "FaultMessage";
	protected static final String BUNDLEID = "BundleId";
	protected static final String SYMBOLICNAME = "SymbolicName";
	protected static final String VERSION = "Version";
	protected static final String BUNDLETYPE = "BundleType";
	protected static final String HEADERS = "Headers";
	protected static final String LOCATION = "Location";
	protected static final String STATE = "State";
	protected static final String REQUESTEDSTATE = "RequestedState";
	protected static final String BUNDLESTARTLEVEL = "StartLevel";
	protected static final String LASTMODIFIED = "LastModified";
	protected static final String BUNDLEINSTANCEID = "InstanceId";
	
	protected static final String WIRES = "Wires";
	protected static final String NAMESPACE = "NameSpace";
	protected static final String REQUIRER = "Requirer";
	protected static final String PROVIDER = "Provider";
	protected static final String WIRESINSTANCEID = "InstanceId";
	
	protected static final String REQUIREMENT = "Requirement";
	protected static final String REQUIREMENTDIRECTIVE = "Directive";
	protected static final String REQUIREMENTATTRIBUTE = "Attribute";
	
	protected static final String CAPABILITY = "Capability";
	protected static final String FILTER = "Filter";
	protected static final String CAPABILITYDIRECTIVE = "Directive";
	protected static final String CAPABILITYATTRIBUTE = "Attribute";
	
	protected static final String SIGNERS = "Signers";
	protected static final String ISTRUSTED = "IsTrusted";
	protected static final String CERTIFICATECHAIN = "CertificateChain";
	protected static final String SIGNERSINSTANCEID = "InstanceId";
	
	protected static final String ENTRIES = "Entries";
	protected static final String PATH = "Path";
	protected static final String CONTENT = "Content";
	protected static final String ENTRIESINSTANCEID = "InstanceId";
	
	protected static final String FRAGMENT = "FRAGMENT";
	protected static final String INSTALLED = "INSTALLED";
	protected static final String RESOLVED = "RESOLVED";
	protected static final String STARTING = "STARTING";
	protected static final String ACTIVE = "ACTIVE";
	protected static final String STOPPING = "STOPPING";
	protected static final String UNINSTALLED = "UNINSTALLED";
	protected static final String FRAMEWORK_NODE_TYPE = "org.osgi/1.0/FrameworkManagementObject";
	protected static final String SERVICE_NAMESPACE = "osgi.wiring.rmt.service";
	protected static final String KEY_OF_RMT_ROOT_URI = "org.osgi.dmt.residential";
	
	protected static final String[] LAUNCHING_PROPERTIES = new String[] {
		"org.osgi.framework.bootdelegation",
		"org.osgi.framework.bsnversion",
		"org.osgi.framework.bundle.parent",
		"org.osgi.framework.command.execpermission",
		"org.osgi.framework.language",
		"org.osgi.framework.library.extensions",
		"org.osgi.framework.os.name",
		"org.osgi.framework.os.version",
		"org.osgi.framework.processor",
		"org.osgi.framework.security",
		"org.osgi.framework.startlevel.beginning",
		"org.osgi.framework.storage",
		"org.osgi.framework.storage.clean",
		"org.osgi.framework.system.packages",
		"org.osgi.framework.system.packages.extra",
		"org.osgi.framework.system.capabilities",
		"org.osgi.framework.system.capabilities.extra",
		"org.osgi.framework.trust.repositories",
		"org.osgi.framework.windowsystem",
		"org.osgi.dmt.residential"
	};
	
	protected FrameworkPlugin plugin;
	protected BundleContext context;
	protected Hashtable bundlesTable = new Hashtable();
	protected Properties properties = null;
	protected boolean managedFlag = false;
	protected int signersInstanceId = 1;
	protected int entriesInstanceId = 1;
	protected int packageWiresInstanceId = 1;
	protected int bundleWiresInstanceId = 1;
	protected int hostWiresInstanceId = 1;
	protected int serviceWiresInstanceId = 1;
	protected int rootLength = 1;

	FrameworkReadOnlySession(FrameworkPlugin plugin, BundleContext context) {
		this.plugin = plugin;
		this.context = context;
		properties = (Properties)System.getProperties().clone();
		for(int i=0;i<LAUNCHING_PROPERTIES.length;i++){
			if ( context.getProperty(LAUNCHING_PROPERTIES[i]) != null )
				properties.put(LAUNCHING_PROPERTIES[i], context.getProperty(LAUNCHING_PROPERTIES[i]));
		}
		String root = System.getProperty(KEY_OF_RMT_ROOT_URI);
		if (root != null) {
			String[] rootArray = pathToArrayUri(root + "/");
			rootLength = rootArray.length;
		}
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			String[] children = new String[4];
			children[0] = FRAMEWORKSTARTLEVEL;
			children[1] = INITIALBUNDLESTARTLEVEL;
			children[2] = BUNDLE;
			children[3] = PROPERTY;
			return children;
		}

		if (path.length == 2) {
			if (path[1].equals(PROPERTY)) {
				if (properties.size() == 0)
					return new String[0];
				String[] children = new String[properties.size()];
				int i = 0;
				for (Enumeration keys = properties.keys(); keys.hasMoreElements(); i++) {
					children[i] = (String) keys.nextElement();
				}
				return children;
			}

			if (path[1].equals(BUNDLE)) {
				if (bundlesTable.size() == 0)
					return new String[0];
				String[] children = new String[bundlesTable.size()];
				int i = 0;
				for (Enumeration keys = bundlesTable.keys(); keys.hasMoreElements(); i++) {
					children[i] = (String)keys.nextElement();
//					children[i] = Uri.decode((String) keys.nextElement());
				}
				return children;
			}
		}
		
		if (path.length == 3 && path[1].equals(BUNDLE)) {
			if(this.bundlesTable.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
				Node node = bs.getLocatonNode();
				return node.getChildNodeNames();
			}
		}

		if(path.length == 4){
			if (path[3].equals(BUNDLETYPE)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					if(bs.getBundleType()!=null){
						String[] type = new String[1];
						type[0] = "0";
						return type;
					}else{
						return new String[0];
					}
				}
			}
			if (path[3].equals(HEADERS)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Dictionary headers = bs.getHeaders();
					String[] children = new String[headers.size()];
					Enumeration keys = headers.keys();
					for(int i=0;keys.hasMoreElements();i++){						
						children[i] = (String)keys.nextElement();
					}
					return children;
				}
			}
			if (path[3].equals(ENTRIES)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Vector entries = bs.getEntries();
					String[] children = new String[entries.size()];
					System.out.println("Bundle: "+path[2]+"  size: "+entries.size());
					for(int i=0;i<entries.size();i++){
						children[i] = Integer.toString(i);
					}
					return children;
				}
			}	
			if (path[3].equals(SIGNERS)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Vector entries = bs.getSigners();
					String[] children = new String[entries.size()];
					for(int i=0;i<entries.size();i++){
						children[i] = Integer.toString(i);
					}
					return children;
				}
			}			
			if (path[3].equals(WIRES)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Map wires = bs.getWires();
					String[] children = new String[wires.size()];
					Iterator it = wires.keySet().iterator();
					for(int i=0;it.hasNext();i++){
						children[i] = (String)it.next();
					}
					return children;
				}
			}
		}
			
		if(path.length == 5){
			if (path[3].equals(ENTRIES)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Vector entries = bs.getEntries();
					try{
						entries.get(Integer.parseInt(path[4]));
						String[] children = new String[3];
						children[0] = PATH;
						children[1] = CONTENT;
						children[2] = ENTRIESINSTANCEID;
						return children;
					}catch(ArrayIndexOutOfBoundsException ae){
						String[] children = new String[0];
						return children;
					}
				}
			}			
			if (path[3].equals(SIGNERS)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Vector signers = bs.getSigners();
					try{
						signers.get(Integer.parseInt(path[4]));
						String[] children = new String[3];
						children[0] = CERTIFICATECHAIN;
						children[1] = ISTRUSTED;
						children[2] = SIGNERSINSTANCEID;
						return children;
					}catch(ArrayIndexOutOfBoundsException ae){
						String[] children = new String[0];
						return children;
					}
				}
			}			
			if (path[3].equals(WIRES)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Map wires = bs.getWires();
					Vector list = (Vector)wires.get(path[4]);
					String[] children = new String[list.size()];
					for(int i=0;i<list.size();i++){
						children[i] = Integer.toString(i);
					}
					return children;
				}
			}
		}
		
		if(path.length == 6){
			if (path[3].equals(SIGNERS)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					Vector signers = bs.getSigners();
					try{
						SignersSubtree ss = (SignersSubtree)signers.get(Integer.parseInt(path[4]));
						Vector chainList = ss.getCertifitateChainList();
						String[] children = new String[chainList.size()];
						for(int i=0;i<chainList.size();i++){
							children[i] = Integer.toString(i);
						}
						return children;
					}catch(ArrayIndexOutOfBoundsException ae){
						String[] children = new String[0];
						return children;
					}
				}
			}			
			if (path[3].equals(WIRES)) {
				if(this.bundlesTable.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
					if(bs.getWires()!=null){
						Vector vec =(Vector)((Map)bs.getWires()).get(path[4]);
						if(vec!=null){
							try{
								vec.get(Integer.parseInt(path[5]));
								String[] children = new String[6];
								children[0] = NAMESPACE;
								children[1] = PROVIDER;
								children[2] = REQUIRER;
								children[3] = WIRESINSTANCEID;
								children[4] = REQUIREMENT;
								children[5] = CAPABILITY;
								return children;
							}catch(ArrayIndexOutOfBoundsException ae){
								String[] children = new String[0];
								return children;
							}
						}
					}
				}
			}
		}
		
		if(path.length == 7 && path[3].equals(WIRES)) {
			if(this.bundlesTable.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
				if(bs.getWires()!=null){
					Vector vec =(Vector)((Map)bs.getWires()).get(path[4]);
					if(vec!=null){
						try{
							vec.get(Integer.parseInt(path[5]));
							if(path[6].equals(REQUIREMENT)){
								String[] children = new String[3];
								children[0] = FILTER;
								children[1] = REQUIREMENTDIRECTIVE;
								children[2] = REQUIREMENTATTRIBUTE;
								return children;
							}
							if(path[6].equals(CAPABILITY)){
								String[] children = new String[2];
								children[0] = CAPABILITYDIRECTIVE;
								children[1] = CAPABILITYATTRIBUTE;
								return children;
							}
						}catch(ArrayIndexOutOfBoundsException ae){
							String[] children = new String[0];
							return children;
						}
					}
				}
			}
		}

		if(path.length == 8 && path[3].equals(WIRES)) {
			if(this.bundlesTable.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
				if(bs.getWires()!=null){
					Vector vec =(Vector)((Map)bs.getWires()).get(path[4]);
					if(vec!=null){
						try{
							WiresSubtree ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
							if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTDIRECTIVE)){
								Map requirementDerective = ws.getRequirementDirective();
								String[] children = new String[requirementDerective.size()];
								Iterator it = requirementDerective.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
							if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTATTRIBUTE)){
								Map requirementAttribute = ws.getRequirementAttribute();
								String[] children = new String[requirementAttribute.size()];
								Iterator it = requirementAttribute.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
							if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYDIRECTIVE)){
								Map capabilityDerective = ws.getCapabilityDirective();
								String[] children = new String[capabilityDerective.size()];
								Iterator it = capabilityDerective.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
							if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYATTRIBUTE)){
								Map capabilityAttribute = ws.getCapabilityAttribute();
								String[] children = new String[capabilityAttribute.size()];
								Iterator it = capabilityAttribute.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
						}catch(ArrayIndexOutOfBoundsException ae){
							String[] children = new String[0];
							return children;
						}
					}
				}
			}
		}

		// other case
		String[] children = new String[0];
		return children;
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) // ./OSGi/Framework
			return new FrameworkMetaNode("Framework Root node.",
					MetaNode.PERMANENT, 
					!FrameworkMetaNode.CAN_ADD,
					!FrameworkMetaNode.CAN_DELETE,
					!FrameworkMetaNode.ALLOW_ZERO,
					!FrameworkMetaNode.ALLOW_INFINITE);

		if (path.length == 2) { // ./OSGi/Framework/...
			if (path[1].equals(FRAMEWORKSTARTLEVEL))
				return new FrameworkMetaNode("StartLevel of Framework.",
						MetaNode.AUTOMATIC, 
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[1].equals(INITIALBUNDLESTARTLEVEL))
				return new FrameworkMetaNode("Initial Bundle StartLevel of Framework.",
						MetaNode.AUTOMATIC, 
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[1].equals(PROPERTY))
				return new FrameworkMetaNode("The Framework Properties.",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[1].equals(BUNDLE))
				return new FrameworkMetaNode("Bundle subtree.",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 3) {
			if (path[1].equals(PROPERTY))
				return new FrameworkMetaNode(
						"The requested start level for the framework.",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[1].equals(BUNDLE))
				return new FrameworkMetaNode("The Map of Location.",
						MetaNode.DYNAMIC,
						FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 4) {
			if (path[3].equals(URL))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(AUTOSTART))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);
			
			if (path[3].equals(FAULTTYPE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[3].equals(FAULTMESSAGE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(BUNDLEID))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null);
			
			if (path[3].equals(SYMBOLICNAME))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(VERSION))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(STATE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(REQUESTEDSTATE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(BUNDLETYPE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[3].equals(HEADERS))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[3].equals(LASTMODIFIED))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_DATE_TIME, null);
			
			if (path[3].equals(LOCATION))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(BUNDLESTARTLEVEL))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);
			
			if (path[3].equals(BUNDLEINSTANCEID))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);
			
			if (path[3].equals(ENTRIES))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[3].equals(SIGNERS))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[3].equals(WIRES))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 5) {
			if (path[3].equals(BUNDLETYPE))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(HEADERS))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(ENTRIES))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[3].equals(SIGNERS))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[3].equals(WIRES))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 6) {
			if (path[5].equals(PATH))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[5].equals(CONTENT))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BINARY, null);
			
			if (path[5].equals(ENTRIESINSTANCEID))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);
			
			if (path[5].equals(ISTRUSTED))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);
			
			if (path[5].equals(SIGNERSINSTANCEID))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);
			
			if (path[5].equals(CERTIFICATECHAIN))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[3].equals(WIRES))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 7) {
			if (path[5].equals(CERTIFICATECHAIN))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(NAMESPACE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(REQUIRER))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(PROVIDER))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(WIRESINSTANCEID))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);
			
			if (path[6].equals(REQUIREMENT))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[6].equals(CAPABILITY))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 8) {
			if (path[6].equals(REQUIREMENT) && path[7].equals(REQUIREMENTDIRECTIVE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[6].equals(REQUIREMENT) && path[7].equals(REQUIREMENTATTRIBUTE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[6].equals(REQUIREMENT) && path[7].equals(FILTER))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(CAPABILITY) && path[7].equals(CAPABILITYDIRECTIVE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[6].equals(CAPABILITY) && path[7].equals(CAPABILITYATTRIBUTE))
				return new FrameworkMetaNode(
						"",
						MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 9) {
			if (path[6].equals(REQUIREMENT) && path[7].equals(REQUIREMENTDIRECTIVE))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(REQUIREMENT) && path[7].equals(REQUIREMENTATTRIBUTE))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(CAPABILITY) && path[7].equals(CAPABILITYDIRECTIVE))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[6].equals(CAPABILITY) && path[7].equals(CAPABILITYATTRIBUTE))
				return new FrameworkMetaNode(
						"",
						MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the Framework.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		return getNodeValue(nodePath).getSize();
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported.");
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property is not supported.");
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1)
			return FRAMEWORK_NODE_TYPE;
		if (path.length == 2){
			if(path[1].equals(BUNDLE))
				return DmtConstants.DDF_MAP;
			if(path[1].equals(PROPERTY))
				return DmtConstants.DDF_MAP;
		}
		if (path.length == 4){
			if(path[3].equals(BUNDLETYPE))
				return DmtConstants.DDF_LIST;
			if(path[3].equals(HEADERS))
				return DmtConstants.DDF_MAP;
			if(path[3].equals(WIRES))
				return DmtConstants.DDF_MAP;
			if(path[3].equals(SIGNERS))
				return DmtConstants.DDF_LIST;
			if(path[3].equals(ENTRIES))
				return DmtConstants.DDF_LIST;
		}
		if (path.length == 6){
			if(path[3].equals(WIRES))
				return DmtConstants.DDF_LIST;
			if(path[5].equals(CERTIFICATECHAIN))
				return DmtConstants.DDF_LIST;
		}
		if (path.length == 8){
			if(path[6].equals(REQUIREMENT) && path[7].equals(REQUIREMENTDIRECTIVE))
				return DmtConstants.DDF_MAP;
			if(path[6].equals(REQUIREMENT) && path[7].equals(REQUIREMENTATTRIBUTE))
				return DmtConstants.DDF_MAP;
			if(path[6].equals(CAPABILITY) && path[7].equals(CAPABILITYDIRECTIVE))
				return DmtConstants.DDF_MAP;
			if(path[6].equals(CAPABILITY) && path[7].equals(CAPABILITYATTRIBUTE))
				return DmtConstants.DDF_MAP;
		}
		if (isLeafNode(nodePath))
			return FrameworkMetaNode.LEAF_MIME_TYPE;
		return FrameworkMetaNode.FRAMEWORK_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return true;

		if (path.length == 2) {
			if (path[1].equals(FRAMEWORKSTARTLEVEL) 
					|| path[1].equals(INITIALBUNDLESTARTLEVEL)
					|| path[1].equals(PROPERTY)
					|| path[1].equals(BUNDLE))
				return true;
		}

		if (path.length == 3) {
			if(path[1].equals(PROPERTY)){
				if (properties.get(path[2]) != null)
					return true;
			}
			if(path[1].equals(BUNDLE)){
				if (bundlesTable.get(path[2]) != null)
					return true;
			}
		}
		
		if (path.length == 4 && path[1].equals(BUNDLE)){
			if(this.bundlesTable.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
				Node node = bs.getLocatonNode();
				if(node.findNode(new String[] {path[3]})!=null)
					return true;
			}
		}
		
		if (path.length == 5 && path[1].equals(BUNDLE)){
			if(this.bundlesTable.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
				if(path[3].equals(BUNDLETYPE)){
					if(bs.getBundleType()!=null)
						return true;
				}
				if(path[3].equals(HEADERS)){
					Dictionary headers = bs.getHeaders();
					if(headers.get(path[4])!=null)
						return true;
				}
				if(path[3].equals(ENTRIES)){
					Vector entries = bs.getEntries();
					try{
						entries.get(Integer.parseInt(path[4]));
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(SIGNERS)){
					Vector signers = bs.getSigners();
					try{
						signers.get(Integer.parseInt(path[4]));
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(WIRES)){
					Map wires = bs.getWires();
					if(wires.get(path[4])!=null)
						return true;
				}
			}
		}
		
		if (path.length == 6 && path[1].equals(BUNDLE)){
			if(this.bundlesTable.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
				if(path[3].equals(ENTRIES)){
					Vector entries = bs.getEntries();
					try{
						entries.get(Integer.parseInt(path[4]));
						if(path[5].equals(PATH)
								|| path[5].equals(CONTENT)
								|| path[5].equals(ENTRIESINSTANCEID))
							return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(SIGNERS)){
					Vector signers = bs.getSigners();
					try{
						signers.get(Integer.parseInt(path[4]));
						if(path[5].equals(ISTRUSTED)
								|| path[5].equals(SIGNERSINSTANCEID)
								|| path[5].equals(CERTIFICATECHAIN))
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					if(vec!=null){
						try{
							vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							return false;
						}						
						return true;
					}
				}
			}
		}
		
		if (path.length == 7 && path[1].equals(BUNDLE)){
			if(this.bundlesTable.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
				if(path[3].equals(SIGNERS)){
					Vector signers = bs.getSigners();
					try{
						SignersSubtree ss = (SignersSubtree)signers.get(Integer.parseInt(path[4]));
						Vector list = ss.getCertifitateChainList();
						list.get(Integer.parseInt(path[6]));
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					if(vec!=null){
						try{
							vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							return false;
						}
						if(path[6].equals(NAMESPACE)
								|| path[6].equals(REQUIREMENT)
								|| path[6].equals(PROVIDER)
								|| path[6].equals(REQUIRER)
								|| path[6].equals(WIRESINSTANCEID)
								|| path[6].equals(CAPABILITY))
						return true;
					}
				}
			}
		}
		
		if (path.length == 8 && path[1].equals(BUNDLE)){
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(path[3].equals(WIRES)){
				Map wires = bs.getWires();
				Vector vec= (Vector)wires.get(path[4]);
				if(vec!=null){
					try{
						vec.get(Integer.parseInt(path[5]));
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
					if(path[7].equals(FILTER)
							|| path[7].equals(REQUIREMENTDIRECTIVE)
							|| path[7].equals(REQUIREMENTATTRIBUTE)
							|| path[7].equals(CAPABILITYDIRECTIVE)
							|| path[7].equals(CAPABILITYDIRECTIVE))
					return true;
				}
			}
		}
		
		if (path.length == 9 && path[1].equals(BUNDLE)){
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(path[3].equals(WIRES)){
				Map wires = bs.getWires();
				Vector vec= (Vector)wires.get(path[4]);
				WiresSubtree ws;
				if(vec!=null){
					try{
						ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
					if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTDIRECTIVE)){
						Map rd = ws.getRequirementDirective();
						return !rd.isEmpty();							
					}
					if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTATTRIBUTE)){
						Map ra = ws.getRequirementAttribute();
						return !ra.isEmpty();
					}
					if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYDIRECTIVE)){
						Map cd = ws.getCapabilityDirective();
						return !cd.isEmpty();
					}
					if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYDIRECTIVE)){
						Map ca = ws.getCapabilityAttribute();
						return !ca.isEmpty();
					}
				}
			}
		}

		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		
		if (path.length == 1)
			return false;
		
		if (path.length == 2){
			if (path[1].equals(FRAMEWORKSTARTLEVEL) 
					|| path[1].equals(INITIALBUNDLESTARTLEVEL))
				return true;
		}			

		if (path.length == 3) {
			if (path[1].equals(PROPERTY))
				return true;
		}

		if (path.length == 4) {
			if (path[3].equals(URL)
					|| path[3].equals(AUTOSTART)
					|| path[3].equals(FAULTTYPE)
					|| path[3].equals(FAULTMESSAGE)
					|| path[3].equals(BUNDLEID)
					|| path[3].equals(SYMBOLICNAME)
					|| path[3].equals(VERSION)
					|| path[3].equals(LOCATION)
					|| path[3].equals(STATE)
					|| path[3].equals(REQUESTEDSTATE)
					|| path[3].equals(LASTMODIFIED)
					|| path[3].equals(BUNDLESTARTLEVEL)
					|| path[3].equals(BUNDLEINSTANCEID))
				return true;
		}
		
		if (path.length == 5) {
			if (path[3].equals(BUNDLETYPE)
					|| path[3].equals(HEADERS))
				return true;
		}

		if (path.length == 6) {
			if (path[5].equals(ISTRUSTED) 
					|| path[5].equals(SIGNERSINSTANCEID)
					|| path[5].equals(PATH)
					|| path[5].equals(CONTENT)
					|| path[5].equals(ENTRIESINSTANCEID))
				return true;
		}
		
		if (path.length == 7) {
			if (path[6].equals(NAMESPACE)
					|| path[6].equals(PROVIDER)
					|| path[6].equals(REQUIRER)
					|| path[6].equals(WIRESINSTANCEID)
					|| path[5].equals(CERTIFICATECHAIN))
				return true;
		}
		
		if (path.length == 8) {
			if (path[7].equals(FILTER))
				return true;
		}
		if (path.length == 9) {
			if (path[7].equals(REQUIREMENTDIRECTIVE)
					|| path[7].equals(REQUIREMENTATTRIBUTE)
					|| path[7].equals(CAPABILITYDIRECTIVE)
					|| path[7].equals(CAPABILITYATTRIBUTE))
				return true;				
		}
		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		
		if (path.length == 2){
			Bundle sysBundle = context.getBundle(0);
			FrameworkStartLevel fs = (FrameworkStartLevel)sysBundle.adapt(FrameworkStartLevel.class);
			if (path[1].equals(FRAMEWORKSTARTLEVEL)){
				int st = fs.getStartLevel();
				return new DmtData(st);
			}
			if (path[1].equals(INITIALBUNDLESTARTLEVEL)){
				int ist = fs.getInitialBundleStartLevel();
				return new DmtData(ist);
			}
		}

		if (path.length == 3) {
			if (path[1].equals(PROPERTY)){
				String value = (String)properties.get(path[2]);
				return new DmtData(value);
			}
		}
		
		if (path.length == 4) {
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(URL))
					return new DmtData(bs.getURL());
				if (path[3].equals(AUTOSTART))
					return new DmtData(bs.getAutoStart());
				if (path[3].equals(FAULTTYPE))
					return new DmtData(bs.getFaultType());
				if (path[3].equals(FAULTMESSAGE))
					return new DmtData(bs.getFaultMassage());
				if (path[3].equals(BUNDLEID))
					return new DmtData(bs.getBundleId());
				if (path[3].equals(SYMBOLICNAME))
					return new DmtData(bs.getSymbolicNmae());
				if (path[3].equals(VERSION))
					return new DmtData(bs.getVersion());
				if (path[3].equals(LOCATION))
					return new DmtData(bs.getLocation());
				if (path[3].equals(STATE))
					return new DmtData(bs.getState());
				if (path[3].equals(REQUESTEDSTATE))
					return new DmtData(bs.getRequestedState());
				if (path[3].equals(LASTMODIFIED))
					return new DmtData(bs.getLastModified());
				if (path[3].equals(BUNDLESTARTLEVEL))
					return new DmtData(bs.getStartLevel());
				if (path[3].equals(BUNDLEINSTANCEID))
					return new DmtData(bs.getInstanceId());
			}
		}
		
		if (path.length == 5){
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(BUNDLETYPE)&&path[4].equals("0")){
					return new DmtData(bs.getBundleType());
				}
				if (path[3].equals(HEADERS)){
					Dictionary dic = bs.getHeaders();
					String value = (String)dic.get(path[4]);
					if(value!=null)
						return new DmtData(value);
				}
			}
		}
		
		if (path.length == 6){
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(ENTRIES)){
					Vector vec = (Vector)bs.getEntries();
					EntrySubtree es = (EntrySubtree)vec.get(Integer.parseInt(path[4]));
					if(path[5].equals(PATH))
						return new DmtData(es.getPath());
					if(path[5].equals(CONTENT))
						return new DmtData(es.getContent());
					if(path[5].equals(ENTRIESINSTANCEID))
						return new DmtData(es.getInstanceId());
				}
				if (path[3].equals(SIGNERS)){
					Vector vec = (Vector)bs.getSigners();
					SignersSubtree ss = (SignersSubtree)vec.get(Integer.parseInt(path[4]));
					if(path[5].equals(ISTRUSTED))
						return new DmtData(ss.isTrusted());
					if(path[5].equals(SIGNERSINSTANCEID))
						return new DmtData(ss.getInstanceId());
				}
			}
		}
		
		if (path.length == 7){
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(SIGNERS)){
					Vector vec = (Vector)bs.getSigners();
					SignersSubtree ss = (SignersSubtree)vec.get(Integer.parseInt(path[4]));
					if(path[5].equals(CERTIFICATECHAIN)){
						Vector cvec = (Vector)ss.getCertifitateChainList();
						String name = (String)cvec.get(Integer.parseInt(path[6]));
						return new DmtData(name);
					}
				}
				if (path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					WiresSubtree ws;
					if(vec!=null){
						try{
							ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified leaf node does not exist in the framework object.");
						}
						if(path[6].equals(NAMESPACE))
							return new DmtData(ws.getNameSpace());
						if(path[6].equals(PROVIDER))
							return new DmtData(ws.getProvider());
						if(path[6].equals(REQUIRER))
							return new DmtData(ws.getRequirer());
						if(path[6].equals(WIRESINSTANCEID))
							return new DmtData(ws.getInstanceId());
					}
				}
			}
		}
		
		if (path.length == 8){
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					WiresSubtree ws;
					if(vec!=null){
						try{
							ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified leaf node does not exist in the framework object.");
						}
						if(path[6].equals(REQUIREMENT)&&path[7].equals(FILTER))
							return new DmtData(ws.getFilter());
					}
				}
			}
		}

		if (path.length == 9){
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					WiresSubtree ws;
					if(vec!=null){
						try{
							ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified leaf node does not exist in the framework object.");
						}
						
						if(path[6].equals(REQUIREMENT)){
							if(path[7].equals(REQUIREMENTDIRECTIVE)){
								Map rd = ws.getRequirementDirective();
								if(!rd.isEmpty())
									return new DmtData(rd.get(path[8]).toString()); 
							}
							if(path[7].equals(REQUIREMENTATTRIBUTE)){
								Map ra = ws.getRequirementAttribute();
								if(!ra.isEmpty())
									return new DmtData(ra.get(path[8]).toString()); 
							}
						}
						if(path[6].equals(CAPABILITY)){
							if(path[7].equals(CAPABILITYDIRECTIVE)){
								Map cd = ws.getCapabilityDirective();
								if(!cd.isEmpty())
									return new DmtData(cd.get(path[8]).toString()); 
							}
							if(path[7].equals(CAPABILITYATTRIBUTE)){
								Map ca = ws.getCapabilityAttribute();
								if(!ca.isEmpty())
									return new DmtData(ca.get(path[8]).toString()); 
							}
						}
					}
				}
			}
		}
		
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified leaf node does not exist in the framework object.");
	}

	protected String[] shapedPath(String[] nodePath) {
		int size = nodePath.length;
		int srcPos = rootLength;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}
	
	protected String[] pathToArrayUri(String path) {
		Vector vector = new Vector();
		while (path.indexOf("/") != -1) {
			String start_path = path.substring(0, path.indexOf("/"));
			vector.add(start_path);
			path = path.substring(path.indexOf("/") + 1, path.length());
		}
		String[] arrayPath = new String[vector.size()];
		int i = 0;
		for (Iterator it = vector.iterator(); it.hasNext(); i++) {
			arrayPath[i] = (String) it.next();
		}
		return arrayPath;
	}
	
	public void bundleChanged(BundleEvent event) {
		if (!this.managedFlag) {
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				String location = Uri.encode(bundles[i].getLocation());
				BundleSubTree bs = new BundleSubTree(bundles[i]);
				this.bundlesTable.put(location, bs);
			}
			this.managedFlag = true;
		}
		
		Bundle bundle = event.getBundle();
		if (event.getType() == BundleEvent.INSTALLED) {
			if(this.bundlesTable.get(Uri.encode(bundle.getLocation()))==null){
				String location = Uri.encode(bundle.getLocation());
				BundleSubTree bs = new BundleSubTree(bundle);
				this.bundlesTable.put(location, bs);
				return;
			}
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(bundle.getLocation());
			if(!bs.getCreateFlag()){
				bs.createNodes(bundle);
			}
		} else if (event.getType() == BundleEvent.RESOLVED) {
			String location = Uri.encode(bundle.getLocation());
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(location);
			bs.createWires();
		} else if (event.getType() == BundleEvent.UNINSTALLED) {
			String location = Uri.encode(bundle.getLocation());
			this.bundlesTable.remove(location);
		}

	}
	
	public void managedWires(){
		for (Enumeration keys = this.bundlesTable.keys(); keys.hasMoreElements();) {
			String location = (String)keys.nextElement();
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(location);
			bs.createWires();
			//XXX
			//if(location.equals("System Bundle"))
				//System.out.println("-----------SYSTEMBUNDLE-----------");
		}
		
	}
	
	protected Map managedWires(Bundle bundle){
		packageWiresInstanceId = 0;
		hostWiresInstanceId = 0;
		bundleWiresInstanceId = 0;
		serviceWiresInstanceId = 0;

		Map wires = new HashMap();

		Vector packageList = new Vector();
		Vector hostList = new Vector();
		Vector bundleList = new Vector();
		Vector serviceList = new Vector();

		BundleWiring wiring = (BundleWiring)bundle.adapt(BundleWiring.class);
		
		List packageRequiredWireList = wiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE);
		packageList.addAll(createWiresSubtree(packageRequiredWireList,BundleRevision.PACKAGE_NAMESPACE));
		List packageProvidedWireList = wiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);
		packageList.addAll(createWiresSubtree(packageProvidedWireList,BundleRevision.PACKAGE_NAMESPACE));
		wires.put(BundleRevision.PACKAGE_NAMESPACE, packageList);
		
		List hostRequiredWireList = wiring.getRequiredWires(BundleRevision.HOST_NAMESPACE);
		hostList.addAll(createWiresSubtree(hostRequiredWireList,BundleRevision.HOST_NAMESPACE));
		List hostProvidedWireList = wiring.getProvidedWires(BundleRevision.HOST_NAMESPACE);
		hostList.addAll(createWiresSubtree(hostProvidedWireList,BundleRevision.HOST_NAMESPACE));
		wires.put(BundleRevision.HOST_NAMESPACE, hostList);
		
		List bundleRequiredWireList = wiring.getRequiredWires(BundleRevision.BUNDLE_NAMESPACE);
		bundleList.addAll(createWiresSubtree(bundleRequiredWireList,BundleRevision.BUNDLE_NAMESPACE));
		List bundleProvidedWireList = wiring.getProvidedWires(BundleRevision.BUNDLE_NAMESPACE);
		bundleList.addAll(createWiresSubtree(bundleProvidedWireList,BundleRevision.BUNDLE_NAMESPACE));
		wires.put(BundleRevision.BUNDLE_NAMESPACE, bundleList);
		
		serviceList = createServiceWiresSubtree(bundle);
		wires.put(SERVICE_NAMESPACE, serviceList);
	
		return wires;
	}
/*
	private Vector createServiceWiresSubtree(Bundle bundle){
		int id = serviceWiresInstanceId;
		Vector list = new Vector();
		try {
			ServiceReference[] references = context.getAllServiceReferences(null, null);
			for(int i=0;i<references.length;i++){
				String registerBundleLocation = references[i].getBundle().getLocation();
				String thisBundleLocation = bundle.getLocation();
				Map directive = new HashMap();
				Map attribute = new HashMap();
				attribute.put(SERVICE_NAMESPACE,references[i].getProperty(Constants.SERVICE_ID));
				attribute.put(Constants.OBJECTCLASS,references[i].getProperty(Constants.OBJECTCLASS));
				String[] keys = references[i].getPropertyKeys();
				for (int j = 0;j<keys.length;j++) {
					attribute.put(keys[j], references[i].getProperty(keys[j]));
				}
				if(registerBundleLocation.equals(thisBundleLocation)){
					Bundle[] usingBundle = references[i].getUsingBundles();
					if(usingBundle!=null){
						for(int k=0;k<usingBundle.length;k++){
							WiresSubtree ws = new WiresSubtree(SERVICE_NAMESPACE,usingBundle[k].getLocation(),
									thisBundleLocation,id++,directive,attribute,directive,attribute,"");
							list.add(ws);
						}
					}
				} else {
					Bundle[] usingBundle = references[i].getUsingBundles();
					if(usingBundle!=null){
						for(int k=0;k<usingBundle.length;k++){
							String usingBundleLocation = usingBundle[k].getLocation();
							if(usingBundleLocation.equals(thisBundleLocation)){
								WiresSubtree ws = new WiresSubtree(SERVICE_NAMESPACE,thisBundleLocation,
										registerBundleLocation,id++,directive,attribute,directive,attribute,"");
								list.add(ws);
							}
						}
					}
				}
			}
		} catch (InvalidSyntaxException e) {
		}
		return list;
	}
*/
	
	private Vector createServiceWiresSubtree(Bundle bundle){
		int id = serviceWiresInstanceId;
		Vector list = new Vector();
		try {
			ServiceReference[] references = context.getAllServiceReferences(null, null);
			for(int i=0;i<references.length;i++){
				String registerBundleLocation = references[i].getBundle().getLocation();
				String thisBundleLocation = bundle.getLocation();
				Map directive = new HashMap();
				Map capabilityAttribute = new HashMap();
				Map requirementAttribute = new HashMap();
				capabilityAttribute.put(SERVICE_NAMESPACE,references[i].getProperty(Constants.SERVICE_ID).toString());
				String[] keys = references[i].getPropertyKeys();
				for (int j = 0;j<keys.length;j++) {
					capabilityAttribute.put(keys[j], references[i].getProperty(keys[j]).toString());
				}
				if(registerBundleLocation.equals(thisBundleLocation)){
					Bundle[] usingBundle = references[i].getUsingBundles();
					String serviceId = references[i].getProperty(Constants.SERVICE_ID).toString();
					String filter = "(service.id="+serviceId+")";
					requirementAttribute.put("Filter", filter);
					if(usingBundle!=null){
						for(int k=0;k<usingBundle.length;k++){
							WiresSubtree ws = new WiresSubtree(SERVICE_NAMESPACE,usingBundle[k].getLocation(),
									thisBundleLocation,id++,directive,requirementAttribute,directive,capabilityAttribute,filter);
							list.add(ws);
						}
					}
				} else {
					Bundle[] usingBundle = references[i].getUsingBundles();
					String serviceId = references[i].getProperty(Constants.SERVICE_ID).toString();
					String filter = "(service.id="+serviceId+")";
					requirementAttribute.put("Filter", filter);
					if(usingBundle!=null){
						for(int k=0;k<usingBundle.length;k++){
							String usingBundleLocation = usingBundle[k].getLocation();
							if(usingBundleLocation.equals(thisBundleLocation)){
								WiresSubtree ws = new WiresSubtree(SERVICE_NAMESPACE,thisBundleLocation,
										registerBundleLocation,id++,directive,requirementAttribute,directive,capabilityAttribute,filter);
								list.add(ws);
							}
						}
					}
				}
			}
		} catch (InvalidSyntaxException e) {
		}
		return list;
	}
	
	private Vector createWiresSubtree(List list,String nameSpace){

		int id = 0;
		Vector vec = new Vector();

		if(nameSpace.equals(BundleRevision.PACKAGE_NAMESPACE)){
			id = packageWiresInstanceId;
		}
		if(nameSpace.equals(BundleRevision.HOST_NAMESPACE)){
			id = hostWiresInstanceId;
		}			
		if(nameSpace.equals(BundleRevision.BUNDLE_NAMESPACE)){
			id = bundleWiresInstanceId;
		}
		Iterator it = list.iterator();
		for(int i=0;it.hasNext();i++){
			BundleWire wire = (BundleWire)it.next();
			BundleCapability capability = wire.getCapability();
			BundleRequirement requirement = wire.getRequirement();
			String providerLocation = wire.getProviderWiring().getBundle().getLocation();
			String requirerLocation = wire.getRequirerWiring().getBundle().getLocation();
			WiresSubtree ws = new WiresSubtree(nameSpace,requirerLocation,providerLocation,
					id++,capability,requirement);
			vec.add(ws);
		}

		return vec;
	}
	
	protected class WiresSubtree{
		String nameSpace = null;
		String requirer = null;
		String provider = null;
		int instanceId = 0;
		Map requirementDirectives = null;
		Map capabilityDirectives = null;
		Map requirementAttributes = null;
		Map capabilityAttributes = null;
		String filter = "";
		
		WiresSubtree(String nameSpace,String require,String provider,int id,
				BundleCapability capability,BundleRequirement requirement){
			this.nameSpace = nameSpace;
			this.requirer = require;
			this.provider = provider;
			this.instanceId = id;
			this.requirementDirectives = requirement.getDirectives();
			this.capabilityDirectives = capability.getDirectives();
			this.requirementAttributes = requirement.getAttributes();
			this.capabilityAttributes = capability.getAttributes();
		}
		
		WiresSubtree(String nameSpace,String require,String provider,int id,
				Map requirementDirectives,Map requirementAttributes,Map capabilityDirectives,
				Map capabilityAttributes,String filter){
			this.nameSpace = nameSpace;
			this.requirer = require;
			this.provider = provider;
			this.instanceId = id;
			this.requirementDirectives = requirementDirectives;
			this.capabilityDirectives = capabilityDirectives;
			this.requirementAttributes = requirementAttributes;
			this.capabilityAttributes = capabilityAttributes;
			this.filter = filter;
		}
		
		protected String getNameSpace(){
			return this.nameSpace;
		}
		protected String getProvider(){
			return this.provider;
		}
		protected String getRequirer(){
			return this.requirer;
		}
		protected int getInstanceId(){
			return this.instanceId;
		}
		protected String getFilter(){
			return this.filter;
		}
		protected Map getRequirementDirective(){
			return this.requirementDirectives;
		}
		protected Map getRequirementAttribute(){
			return this.requirementAttributes;
		}
		protected Map getCapabilityDirective(){
			return this.capabilityDirectives;
		}
		protected Map getCapabilityAttribute(){
			return this.capabilityAttributes;
		}
	}
	
	
	protected class BundleSubTree{
		Bundle bundle = null;
		String location = null;
		String url = "";
		boolean autoStart = false;
		int type = -1;
		String message = "";
		String requestedState = "";
		Vector entries = null;
		Vector signers = null;
		Map wires = null;
		Node locationNode = null;
		int bundleStartLevelTmp = 0;
		boolean createFlag = false;
		
		BundleSubTree(String bundleLocation){
			this.location = Uri.encode(bundleLocation);
			
			locationNode = new Node(this.location,null,true);
			locationNode.addNode(new Node(URL,null,false));
			locationNode.addNode(new Node(AUTOSTART,null,false));
			locationNode.addNode(new Node(LOCATION,null,false));
			locationNode.addNode(new Node(REQUESTEDSTATE,null,false));
			locationNode.addNode(new Node(BUNDLESTARTLEVEL,null,false));
			locationNode.addNode(new Node(BUNDLEINSTANCEID,null,false));
		}		
		BundleSubTree(Bundle bundleObj){
			this.bundle = bundleObj;
			this.location = Uri.encode(bundle.getLocation());
			this.createFlag = true;
			
			locationNode = new Node(this.location,null,true);
			locationNode.addNode(new Node(URL,null,false));
			locationNode.addNode(new Node(AUTOSTART,null,false));
			locationNode.addNode(new Node(LOCATION,null,false));
			locationNode.addNode(new Node(REQUESTEDSTATE,null,false));
			locationNode.addNode(new Node(BUNDLESTARTLEVEL,null,false));
			locationNode.addNode(new Node(BUNDLEINSTANCEID,null,false));
			
			locationNode.addNode(new Node(BUNDLEID,null,false));
			locationNode.addNode(new Node(SYMBOLICNAME,null,false));
			locationNode.addNode(new Node(VERSION,null,false));
			locationNode.addNode(new Node(STATE,null,false));
			locationNode.addNode(new Node(LASTMODIFIED,null,false));
			locationNode.addNode(new Node(FAULTTYPE,null,false));
			locationNode.addNode(new Node(FAULTMESSAGE,null,false));
			
			locationNode.addNode(new Node(BUNDLETYPE,null,true));
			locationNode.addNode(new Node(HEADERS,null,true));
			locationNode.addNode(new Node(ENTRIES,null,true));
			locationNode.addNode(new Node(SIGNERS,null,true));
			locationNode.addNode(new Node(WIRES,null,true));	
			this.entries = managedEntries(null,this.bundle,"");
			this.signers = managedSigners(this.bundle);
		}
		
		protected void createNodes(Bundle bundle){
			this.bundle = bundle;
			this.createFlag = true;
			locationNode.addNode(new Node(BUNDLEID,null,false));
			locationNode.addNode(new Node(SYMBOLICNAME,null,false));
			locationNode.addNode(new Node(VERSION,null,false));
			locationNode.addNode(new Node(STATE,null,false));
			locationNode.addNode(new Node(LASTMODIFIED,null,false));
			locationNode.addNode(new Node(FAULTTYPE,null,false));
			locationNode.addNode(new Node(FAULTMESSAGE,null,false));
			
			locationNode.addNode(new Node(BUNDLETYPE,null,true));
			locationNode.addNode(new Node(HEADERS,null,true));
			locationNode.addNode(new Node(ENTRIES,null,true));
			locationNode.addNode(new Node(SIGNERS,null,true));
			locationNode.addNode(new Node(WIRES,null,true));	
			this.entries = managedEntries(null,this.bundle,"");
			this.signers = managedSigners(this.bundle);
		}
		
		protected boolean getCreateFlag(){
			return this.createFlag;
		}
		
		protected void createWires(){
			if(this.bundle.getState()<=2)
				return;
			this.wires = managedWires(this.bundle);
		}
		
		protected Bundle getBundleObj(){
			return this.bundle;
		}
		
		protected Node getLocatonNode(){
			return this.locationNode;
		}
		
		protected void setURL(String url){
			this.url = url;
		}
		protected String getURL(){
			return this.url;
		}
		protected void setAutoStart(boolean autoStart){
			this.autoStart = autoStart;
		}
		protected boolean getAutoStart(){
			return this.autoStart;
		}
		protected void setFaultType(int type){
			this.type = type;
		}
		protected int getFaultType(){
			return this.type;
		}
		protected void setFaultMassage(String message){
			this.message = message;
		}
		protected String getFaultMassage(){
			return this.message;
		}
		protected long getBundleId(){
			return this.bundle.getBundleId();
		}
		protected String getSymbolicNmae(){
			return this.bundle.getSymbolicName();
		}
		protected String getVersion(){
			return this.bundle.getVersion().toString();
		}
		protected String getBundleType(){
			BundleRevision rev = (BundleRevision)this.bundle.adapt(BundleRevision.class);
			int bundleType = rev.getTypes();
			if(bundleType==1)
				return FRAGMENT;
			else
				return null;
		}
		protected Dictionary getHeaders(){
			if(this.bundle==null)
				return null;
			else
				return this.bundle.getHeaders();
		}
		protected String getLocation(){
			return Uri.decode(this.location);
		}
		protected String getState(){
			int state = this.bundle.getState();
			if(state==2)
				return INSTALLED;
			else if(state==4)
				return RESOLVED;
			else if(state==8)
				return STARTING;
			else if(state==32)
				return ACTIVE;
			else if(state==16)
				return STOPPING;
			else
				return null;
		}
		protected void setRequestedState(String state){
			this.requestedState = state;
		}
		protected String getRequestedState(){
			return this.requestedState;
		}
		protected Date getLastModified(){
			return new Date(this.bundle.getLastModified());
		}
		protected int getStartLevel(){
			BundleStartLevel sl = (BundleStartLevel)this.bundle.adapt(BundleStartLevel.class);
			return sl.getStartLevel();
		}
		protected int getStartLevelTmp(){
			return this.bundleStartLevelTmp;
		}
		protected void setStartLevel(int stl){
			this.bundleStartLevelTmp = stl;
			BundleStartLevel sl = (BundleStartLevel)this.bundle.adapt(BundleStartLevel.class);
			sl.setStartLevel(stl);
		}
		protected int getInstanceId(){
			long bundleId = this.bundle.getBundleId() + 1;
			return Integer.parseInt(Long.toString(bundleId));
		}
		protected Vector getEntries(){
			return entries;
		}
		protected Vector getSigners(){
			return signers;
		}
		protected Map getWires(){
			return wires;
		}
	}
	
	protected class Node {
		static final String INTERIOR = "Interiror";
		static final String LEAF = "leaf";
		private String name;
		private String type;
		private Vector children = new Vector();

		Node(String name, Node[] children, boolean nodeType){
			this.name = name;
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					this.children.add(children[i]);
				}
			} else
				this.children = new Vector();
			if(nodeType)
				type = INTERIOR;
			else
				type = LEAF;
		}
		
		protected Node findNode(String[] path) {
			for (int i = 0; i < children.size(); i++) {
				if ((((Node) children.get(i)).getName()).equals(path[0])) {
					if (path.length == 1) {
						return (Node) children.get(i);
					} else {
						String[] nextpath = new String[path.length - 1];
						for (int x = 1; x < path.length; x++) {
							nextpath[x - 1] = path[x];
						}
						return ((Node) children.get(i)).findNode(nextpath);
					}
				}
			}
			return null;
		}
		
		protected String getName() {
			return name;
		}

		protected void addNode(Node add) {
			children.add(add);
		}

		protected void deleteNode(Node del) {
			children.remove(del);
		}

		protected Node[] getChildren() {
			Node[] nodes = new Node[children.size()];
			for (int i = 0; i < children.size(); i++) {
				nodes[i] = ((Node) children.get(i));
			}
			return nodes;
		}
		
		protected String[] getChildNodeNames(){
			String[] name = new String[children.size()];
			for (int i = 0; i < children.size(); i++) {
				Node node = ((Node) children.get(i));
				name[i] = node.getName();
			}
			return name;
		}

		protected String getType() {
			return type;
		}
	}
	
	protected Vector managedSigners(Bundle bundle){
		Map signersAll = bundle.getSignerCertificates(Bundle.SIGNERS_ALL);
		Map signersTrusted = bundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		Vector certList = new Vector();
		Iterator it = signersAll.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			Vector certChainList = new Vector();
			X509Certificate cert = (X509Certificate) it.next();
			List certificateChane = (List) signersAll.get(cert);
			for (Iterator itCert = certificateChane.iterator(); itCert.hasNext();) {
				X509Certificate certs = (X509Certificate) itCert.next();
				certChainList.add(certs.getIssuerDN().getName());
			}
			if(signersTrusted.get(cert)!=null){
				SignersSubtree signersobj = new SignersSubtree(true,signersInstanceId,certChainList);
				signersInstanceId++;
				certList.add(signersobj);
			}else{
				SignersSubtree signersobj = new SignersSubtree(false,signersInstanceId,certChainList);
				signersInstanceId++;
				certList.add(signersobj);
			}
		}
		return certList;
	}

	protected class SignersSubtree{
		boolean trust = false;
		int id = 0;
		Vector certChainList = null;
		SignersSubtree(boolean trust, int id, Vector certChainList){
			this.certChainList = certChainList;
			this.id = id;
			this.trust = trust;			
		}
		protected int getInstanceId(){
			return id;
		}
		protected boolean isTrusted(){
			return trust;
		}
		protected Vector getCertifitateChainList(){
			return certChainList;
		}
	}
	
	protected Vector managedEntries(Vector entries, Bundle bundle, String p){
		if(entries==null){
			entries = new Vector();
		}
		Vector entryPathes = new Vector();
		entryPathes = bundleEntry(entryPathes,bundle,p);
		Iterator ite = entryPathes.iterator();
		while(ite.hasNext()){
			String path = (String)ite.next();
			try {
				BufferedInputStream bis = new BufferedInputStream(bundle.getEntry(Uri.decode(path)).openStream());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int b = -1;
				while ( (b = bis.read()) != -1 ) 
					bos.write(b);
				EntrySubtree entriesObj = new EntrySubtree(path,bos.toByteArray(),entriesInstanceId);
				bis.close();
				bos.close();
				entriesInstanceId++;
				entries.add(entriesObj);
			}catch (IOException ioe) {
				continue;
			}
		}
		return entries;
	}
	
	private Vector bundleEntry(Vector entry, Bundle bundle, String p) {
		Enumeration pathes = bundle.getEntryPaths(p);
		while (pathes.hasMoreElements()) {
			String path = (String)pathes.nextElement();
			if ( path.endsWith("/"))
				bundleEntry(entry, bundle, path);
			else {
				entry.add(Uri.encode(path));
			}
		}
		return entry;
	}
	
	protected class EntrySubtree{
		String path = null;
		byte[] content = null;
		int id = -1;
		EntrySubtree(String path, byte[] content, int id){
			this.path = path;
			this.content = content;
			this.id = id;
		}
		protected String getPath(){
			return path;
		}
		protected byte[] getContent(){
			return content;
		}
		protected int getInstanceId(){
			return id;
		}
	}
}

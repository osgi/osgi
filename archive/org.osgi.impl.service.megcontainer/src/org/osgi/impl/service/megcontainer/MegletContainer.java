/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
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
package org.osgi.impl.service.megcontainer;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;

import org.osgi.framework.*;
import org.osgi.meglet.Meglet;
import org.osgi.service.application.*;
import org.osgi.service.application.meglet.*;
import org.osgi.service.event.*;
import org.osgi.service.log.LogService;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.component.*;
import org.w3c.dom.*;

class EventSubscribe {
	public final static int	START			= 1;
	public final static int	STOP			= 2;
	public final static int	SUSPEND		= 3;
	public final static int	RESUME		= 4;
	public final static int	HANDLE		= 5;
	public String[]			eventTopic;
	public int[]			eventAction;
}

class Dependencies {
	public String[]					requiredServices;
	public String[]					requiredPackages;
}

class MEGBundleDescriptor {
	public ApplicationDescriptor[] applications;
	public ServiceRegistration[]	serviceRegistrations;
	public EventSubscribe[]			eventSubscribes;
	public Dependencies[]			dependencies;
	public long						bundleID;
}

/**
 * The realization of the MEG container
 */

public class MegletContainer implements BundleListener, EventHandler {
	private BundleContext					bc;
	private Vector								bundleIDs;
	private Hashtable							bundleHash;
	private int										height;
	private int										width;

	public MegletContainer( BundleContext bc ) throws Exception {
		this.bc = bc;
		bundleHash = new Hashtable();
		bundleIDs = loadVector("BundleIDs");

		for (int j = 0; j < bundleIDs.size(); j++)
		{
			Bundle bundle = bc.getBundle( Long.parseLong((String) bundleIDs.get(j)) );
			if( bundle == null || bundle.getState() == Bundle.UNINSTALLED )
			{
				bundleIDs.remove( j-- );
				saveVector(bundleIDs, "BundleIDs");
			}
		}

		for (int i = 0; i != bundleIDs.size(); i++)
			registerBundle(Long.parseLong((String) bundleIDs.get(i)));
		bc.addBundleListener(this);
		Bundle [] bundles = bc.getBundles();

		for ( int i=0; i<bundles.length; i++ ) {
			String id = bundles[i].getBundleId()+"";
			if ( ! bundleIDs.contains(id) && bundles[i].getBundleId()!=0) {
				checkAndRegister(bundles[i]);
			}
		}
	}

	public ApplicationDescriptor[] installApplication(long bundleID)
			throws IOException, Exception {

		if( bundleIDs.contains(Long.toString(bundleID)))
			return null;

		ApplicationDescriptor[] appDescs = registerBundle(bundleID);
		if (appDescs == null)
			throw new Exception("Not a valid MEG bundle!");

		bundleIDs.add(Long.toString(bundleID));
		saveVector(bundleIDs, "BundleIDs");

		return appDescs;
	}
    
  public Meglet createMegletInstance( MegletDescriptorImpl appDesc, boolean resume ) throws Exception {
		MEGBundleDescriptor desc = getBundleDescriptor( appDesc.getBundleId() );
		int i = getApplicationIndex( desc, appDesc.getMegletDescriptor() );
        
    String depResult = checkDependencies( appDesc, desc.dependencies[i]);      
		if ( depResult != null )
    {
      System.err.println( depResult );
			throw new Exception("Can't start the application because of failed dependencies!");
    }

		Meglet app;
		
		ServiceReference components[] = bc.getServiceReferences( ComponentFactory.class.getName(),
				"(" + ComponentConstants.COMPONENT_NAME + "=" + appDesc.getComponentName() + ")" );
		if( components == null || components.length == 0 )
			throw new Exception( "SCR component not found, cannot start without SCR!" );

		ComponentFactory cf = (ComponentFactory)bc.getService( components[ 0 ] );
		ComponentInstance ci = cf.newInstance( null );
		app = (Meglet) ci.getInstance();
		bc.ungetService( components[ 0 ] );
		
		Method registerListenerMethod = Meglet.class.getDeclaredMethod( "registerForEvents",
										new Class [] { String.class, String.class } );
		registerListenerMethod.setAccessible( true );

		Vector eventTopic = new Vector();
		if (desc.eventSubscribes[i] != null
				&& desc.eventSubscribes[i].eventTopic != null)
			for (int j = 0; j != desc.eventSubscribes[i].eventTopic.length; j++)
				if (desc.eventSubscribes[i].eventAction[j] == EventSubscribe.HANDLE)
					registerListenerMethod.invoke( app, new Object [] {
							desc.eventSubscribes[i].eventTopic[j], null } );

		return app;
	}

	private MEGBundleDescriptor getBundleDescriptor( long bundleID ) throws Exception {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
				.get(new Long(bundleID));
		if (desc == null)
			throw new Exception(
					"Application wasn't installed onto the meglet container!");
		return desc;
	}

	private int getApplicationIndex( MEGBundleDescriptor desc,
									 ApplicationDescriptor appDesc ) throws Exception {
		int i;
		for (i = 0; i != desc.applications.length; i++)
			if (desc.applications[i] == appDesc) {
				return i;
			}
		throw new Exception(
				"Application wasn't installed onto the meglet container!");
	}

	private String checkDependencies(MegletDescriptorImpl appDesc, Dependencies deps) {
		try {
			ServiceReference components[] = bc.getServiceReferences( ComponentFactory.class.getName(),
					"(" + ComponentConstants.COMPONENT_NAME + "=" + appDesc.getComponentName() + ")" );
			if( components == null || components.length == 0 )
				return "Cannot find launchable SCR component! Please check the dependencies.";
			
			if (deps.requiredServices != null) {
				for (int i = 0; i != deps.requiredServices.length; i++) {
					ServiceReference servRef = bc
							.getServiceReference(deps.requiredServices[i]);
					if (servRef == null) {
						return "Cannot find \"" + deps.requiredServices[i] + "\" service for launch!";
					}
				}
			}
			if (deps.requiredPackages != null) {
				ServiceReference pkgAdminSrv = bc
						.getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
				if (pkgAdminSrv == null)
					return "Cannot find PackageAdmin to check the required packages!";
				PackageAdmin pkgAdmin = (PackageAdmin) bc
						.getService(pkgAdminSrv);
				for (int j = 0; j != deps.requiredPackages.length; j++)
					if (pkgAdmin.getExportedPackage(deps.requiredPackages[j]) == null) {
						bc.ungetService(pkgAdminSrv);
						return "Cannot find \"" + deps.requiredPackages[j] + "\" package for launch!";
					}
				bc.ungetService(pkgAdminSrv);
			}
			return null;
		}
		catch (Exception e) {
			log(bc, LogService.LOG_ERROR,
				"Exception occurred at checking the dependencies of a meglet!", e);
			return "Exception occurred at checking the dependencies!";
		}
	}

	public boolean isLaunchable( MegletDescriptorImpl appDesc ) {
		try {
			if( appDesc.isLocked() )
				return false;
			MEGBundleDescriptor desc = getBundleDescriptor(
								((MegletDescriptorImpl)appDesc).getBundleId() );
			if( desc == null )
				return false;
			int i = getApplicationIndex( desc, appDesc.getMegletDescriptor() );
			if( i == -1 )
				return false;
            
      String depResult = checkDependencies( appDesc, desc.dependencies[i] );
      if( depResult != null) {
//        System.err.println( depResult );
      }        
			return true;
		}
		catch (Exception e) {
			log(bc, LogService.LOG_ERROR,
				"Exception occurred at checking if the meglet is launchable!", e);
			return false;
		}
	}

	public void unregisterAllApplications() throws Exception {
		for (int i = 0; i != bundleIDs.size(); i++)
			unregisterApplicationDescriptors(Long.parseLong((String) bundleIDs
					.get(i)));
	}

	private ApplicationDescriptor[] registerBundle(long bundleID) {
		ApplicationDescriptor[] appDescs = parseApplicationXML(bundleID);
		if (appDescs == null)
			return null;
		registerApplicationDescriptors(bundleID);
		return appDescs;
	}

	private void registerApplicationDescriptors(long bundleID) {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
				.get(new Long(bundleID));
		if (desc == null)
			return;
		for (int i = 0; i != desc.applications.length; i++) {
			if (desc.serviceRegistrations[i] != null) {
				desc.serviceRegistrations[i].unregister();
				desc.serviceRegistrations[i] = null;
			}
			Dictionary properties = new Hashtable(desc.applications[i]
					.getProperties((Locale.getDefault()).getLanguage()));
			
			String pid = (String)properties.get( ApplicationDescriptor.APPLICATION_PID );
			
			properties.put( Constants.SERVICE_PID, pid );
			desc.serviceRegistrations[i] = bc.registerService(
					"org.osgi.service.application.ApplicationDescriptor",
					desc.applications[i], properties);
		}
	}

	private void unregisterApplicationDescriptors(long bundleID) {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
				.get(new Long(bundleID));
		if (desc == null)
			return;
		for (int i = 0; i != desc.serviceRegistrations.length; i++) {
			if (desc.serviceRegistrations[i] != null) {
				desc.serviceRegistrations[i].unregister();
				desc.serviceRegistrations[i] = null;
			}
		}
	}

	public void bundleChanged(BundleEvent event) {
		long bundleID = event.getBundle().getBundleId();
		String bundleStr = Long.toString(bundleID);
		if (bundleIDs.contains(bundleStr)) {
			switch (event.getType()) {
				case BundleEvent.STARTED :
					registerApplicationDescriptors(bundleID);
					break;
				case BundleEvent.STOPPED :
					unregisterApplicationDescriptors(bundleID);
					break;
				case BundleEvent.UNINSTALLED :
					bundleIDs.remove(bundleStr);
					saveVector(bundleIDs, "BundleIDs");
					unregisterApplicationDescriptors(bundleID);
					bundleHash.remove(new Long(bundleID));
					break;
				case BundleEvent.UPDATED :
					unregisterApplicationDescriptors(bundleID);
					registerApplicationDescriptors(bundleID);
					break;
			}
		} else {
			if ( event.getType() == BundleEvent.INSTALLED )
				checkAndRegister( event.getBundle() );
		}
	}

	void checkAndRegister(Bundle b) {
		try {
			URL url = b.getResource("META-INF/meglets.xml");
			if ( url != null )
				installApplication(b.getBundleId());
		}
		catch (Exception e) {
			log(bc, LogService.LOG_ERROR,
				"Exception occurred at installing a meglet!", e);
		}
	}

	private Vector loadVector(String fileName) {
		Vector resultVector = new Vector();
		try {
			File vectorFile = bc.getDataFile(fileName);
			if (vectorFile.exists()) {
				FileInputStream stream = new FileInputStream(vectorFile);
				String codedIds = "";
				byte[] buffer = new byte[1024];
				int length;
				while ((length = stream.read(buffer, 0, buffer.length)) > 0)
					codedIds += new String(buffer);
				stream.close();
				if (!codedIds.equals("")) {
					int index = 0;
					while (index != -1) {
						int comma = codedIds.indexOf(',', index);
						String name;
						if (comma >= 0)
							name = codedIds.substring(index, comma);
						else
							name = codedIds.substring(index);
						resultVector.add(name.trim());
						index = comma;
					}
				}
			}
		}
		catch (Exception e) {
			log(bc, LogService.LOG_ERROR,
				"Exception occurred at loading '" + fileName + "'!", e);
		}
		return resultVector;
	}

	private void saveVector(Vector vector, String fileName) {
		try {
			File vectorFile = bc.getDataFile(fileName);
			FileOutputStream stream = new FileOutputStream(vectorFile);
			for (int i = 0; i != vector.size(); i++)
				stream.write((((i == 0) ? "" : ",") + (String) vector.get(i))
						.getBytes());
			stream.close();
		}
		catch (Exception e) {
			log(bc, LogService.LOG_ERROR,
				"Exception occurred at saving '" + fileName + "'!", e);
		}
	}

	private String getAttributeValue( Node node, String attribName ) {
		NamedNodeMap nnm = node.getAttributes();

		if(nnm != null ) {
			int len = nnm.getLength() ;

			for ( int i = 0; i < len; i++ ) {
				Attr attr = (Attr)nnm.item(i);
				if( attr.getNodeName().equals( attribName ) )
					return attr.getNodeValue();
			}
		}

		return null;
	}

	private ApplicationDescriptor[] parseApplicationXML(long bundleID) {
		try {
			ServiceReference domParserReference = bc
				.getServiceReference( DocumentBuilderFactory.class.getName() );
			if (domParserReference == null) {
				log(bc, LogService.LOG_ERROR, "Cannot find the DOM parser factory!", null );
				return null;
			}
			DocumentBuilderFactory domFactory = (DocumentBuilderFactory) bc
				.getService( domParserReference );
      boolean validating = domFactory.isValidating();
      domFactory.setValidating( false );
			DocumentBuilder domParser = domFactory.newDocumentBuilder();
      domFactory.setValidating( validating );
			bc.ungetService( domParserReference );
			if( domParser == null ) {
				log(bc, LogService.LOG_ERROR, "Cannot create DOM parser!", null );
				return null;
			}

			URL url = bc.getBundle(bundleID).getResource(
					"META-INF/meglets.xml");
			InputStream in = url.openStream();

			//pkr: added null check
			if ( in == null )
				return null;

			Document doc = domParser.parse( in );

			NodeList listNodeList = null;
			
			listNodeList = doc.getElementsByTagName( "descriptor" );

			if( listNodeList.getLength() != 1 )
				throw new Exception( "One descriptor must be present in the meglets.xml file!" );

			Node listNode = listNodeList.item( 0 );
			Node applicationNode = listNode.getFirstChild();

			LinkedList appVector = new LinkedList();
			LinkedList eventVector = new LinkedList();
			LinkedList dependencyVector = new LinkedList();

			while( applicationNode != null ) {
				if( applicationNode.getNodeType() == Node.ELEMENT_NODE  && applicationNode.getNodeName().equals( "application" ) ) {
					
					Properties props = new Properties();
					Hashtable names = new Hashtable();
					Hashtable icons = new Hashtable();
					String defaultLanguage = null;
					String startClass = null;
					String componentName = null;
					String uniqueID = null;
					props.setProperty("application.bundle.id", Long.toString(bundleID));
					LinkedList eventTopic = new LinkedList();
					LinkedList eventAction = new LinkedList();
					LinkedList requiredServices = new LinkedList();
					LinkedList requiredPackages = new LinkedList();
					
					NodeList nodeList = applicationNode.getChildNodes();

					for(int i=0; i < nodeList.getLength(); i++ ) {
						Node node = nodeList.item( i );
						if( node.getNodeType() == Node.ELEMENT_NODE ) {
							if( node.getNodeName().equals( "vendor" ) )
								props.setProperty("application.vendor",
										getAttributeValue( node, "value" ) );
							if( node.getNodeName().equals( "visible" ) )
								props.setProperty("application.visible",
										getAttributeValue( node, "value" ) );
							if( node.getNodeName().equals( "version" ) )
								props.setProperty("application.version",
										getAttributeValue( node, "value" ) );
							if( node.getLocalName().equals( "component" ) &&
									node.getPrefix() != null && node.getPrefix().equals( "scr" )) {
								uniqueID = getAttributeValue( node, "factory" );

								componentName = getAttributeValue( node, "name" );
								
								NodeList childNodes = node.getChildNodes();
								
								for(int j=0; j < childNodes.getLength(); j++ ) {
									Node childNode = childNodes.item( j );

									if( childNode.getNodeName().equals( "implementation" ) )
										startClass = getAttributeValue( childNode, "class" );
								}
							}
							if( node.getNodeName().equals( "required_services" ) ||
								node.getNodeName().equals( "required_physical_resource" ) ) {
								String services = getAttributeValue( node, "value" );
								int ndx = 0;
								while (ndx != -1) {
									int nextNdx = services.indexOf(',', ndx);
									String splitted;
									if (nextNdx == -1)
										splitted = services.substring(ndx).trim();
									else
										splitted = services.substring(ndx,
												nextNdx++).trim();
									if (splitted.length() != 0)
										requiredServices.add(splitted);
									ndx = nextNdx;
								}
							}
							if( node.getNodeName().equals( "required_logical_resource" ) ) {
								String packages = getAttributeValue( node, "value" );
								int ndx = 0;
								while (ndx != -1) {
									int nextNdx = packages.indexOf(',', ndx);
									String splitted;
									if (nextNdx == -1)
										splitted = packages.substring(ndx).trim();
									else
										splitted = packages.substring(ndx,
											nextNdx++).trim();
									if (splitted.length() != 0)
										requiredPackages.add(splitted);
									ndx = nextNdx;
								}
							}
							if( node.getNodeName().equals( "subscribe" ) ) {
								String topicString = getAttributeValue( node, "event" );
								String actionString = getAttributeValue( node, "action" );

								if (topicString == null || actionString == null)
									throw new Exception("Invalid Subscribe");

								int action;
								if (actionString.equals("start"))
									action = EventSubscribe.START;
								else if (actionString.equals("stop"))
									action = EventSubscribe.STOP;
								else if (actionString.equals("suspend"))
									action = EventSubscribe.SUSPEND;
								else if (actionString.equals("resume"))
									action = EventSubscribe.RESUME;
								else if (actionString.equals("handle"))
									action = EventSubscribe.HANDLE;
								else
									throw new Exception( "Invalid Action" );

								eventTopic.add(topicString);
								eventAction.add(new Integer(action));
							}
							if( node.getNodeName().equals( "locale" ) ) {
								String lang = getAttributeValue( node, "name" );
								
								String dflt = getAttributeValue( node, "default" );
								if( dflt != null && dflt.equalsIgnoreCase( "true" ) )
									defaultLanguage = lang;
								
								NodeList childNodes = node.getChildNodes();

								for(int j=0; j < childNodes.getLength(); j++ ) {
									Node childNode = childNodes.item( j );

									if( childNode.getNodeName().equals( "name" ) )
										names.put( lang, getAttributeValue( childNode, "value" ) );
									if( childNode.getNodeName().equals( "icon" ) ) {
										String iconPath = getAttributeValue( childNode, "value" );
										URL iconUrl = bc.getBundle(bundleID).getResource(iconPath);
										icons.put(lang, iconUrl.toString());
									}
								}
							}
						}
					}
					
					if( componentName == null )
						throw new Exception( "Component name of the scr:component node is missing!" );
					if( uniqueID == null )
						throw new Exception( "Factory attribute of the scr:component node is missing!" );
					props.put( ApplicationDescriptor.APPLICATION_PID, uniqueID );
					
					if (startClass != null) {
						EventSubscribe subscribe = new EventSubscribe();
						if (eventTopic.size() != 0) {
							subscribe.eventTopic = new String[eventTopic.size()];
							subscribe.eventAction = new int[eventTopic.size()];
							int topicNumber = eventTopic.size();
							for (int q = 0; q != topicNumber; q++) {
								subscribe.eventTopic[q] = (String) eventTopic
										.removeFirst();
								subscribe.eventAction[q] = ((Integer) eventAction
										.removeFirst()).intValue();
							}
						}
						
						if( names.size() == 0 )
							throw new Exception( "No locale information given for the Meglet!" );						
						if( defaultLanguage == null ) {
							Enumeration enum = names.keys();
							if( enum.hasMoreElements() ) {
								defaultLanguage = (String)enum.nextElement();
							}
						}
						
						Dependencies deps = new Dependencies();
						int m;
						deps.requiredServices = new String[requiredServices
								.size()];
						for (m = 0; m != requiredServices.size(); m++)
							deps.requiredServices[m] = (String) requiredServices
									.get(m);
						deps.requiredPackages = new String[requiredPackages
								.size()];
						for (m = 0; m != requiredPackages.size(); m++)
							deps.requiredPackages[m] = (String) requiredPackages
									.get(m);
						eventVector.add(subscribe);
						appVector.add( createMegletDescriptorByReflection( props,
								names, icons, defaultLanguage, startClass, componentName, bc.getBundle(bundleID) ));
						dependencyVector.add(deps);
					}
				}
				applicationNode = applicationNode.getNextSibling();
			}
			ApplicationDescriptor[] descs = new ApplicationDescriptor[appVector
					.size()];
			int applicationNum = appVector.size();
			for (int k = 0; k != applicationNum; k++)
				descs[k] = (ApplicationDescriptor) appVector.removeFirst();
			MEGBundleDescriptor descriptor = new MEGBundleDescriptor();
			descriptor.applications = new ApplicationDescriptor[applicationNum];
			descriptor.eventSubscribes = new EventSubscribe[applicationNum];
			descriptor.dependencies = new Dependencies[applicationNum];
			for (int l = 0; l != applicationNum; l++) {
				descriptor.applications[l] = descs[l];
				descriptor.eventSubscribes[l] = (EventSubscribe) eventVector
						.removeFirst();
				descriptor.dependencies[l] = (Dependencies) dependencyVector
						.removeFirst();
			}
			descriptor.serviceRegistrations = new ServiceRegistration[applicationNum];
			descriptor.bundleID = bundleID;
			bundleHash.put(new Long(bundleID), descriptor);
			return descs;
		}
		catch (Throwable e) {
			log(bc, LogService.LOG_ERROR,
				"Exception occurred at parsing a meglet bundle!", e);
			return null;
		}
	}

	public void handleEvent(Event event) {

		try {
			Enumeration megBundles = bundleHash.keys();
			while (megBundles.hasMoreElements()) {
				Object key = megBundles.nextElement();
				MEGBundleDescriptor bundleDesc = (MEGBundleDescriptor) bundleHash
						.get(key);
				for (int i = 0; i != bundleDesc.eventSubscribes.length; i++) {
					if (bundleDesc.eventSubscribes[i] != null
							&& bundleDesc.eventSubscribes[i].eventTopic != null)
						for (int j = 0; j != bundleDesc.eventSubscribes[i].eventTopic.length; j++) {
							Filter topicFilter = bc
									.createFilter("(" + EventConstants.EVENT_TOPIC + "="
											+ bundleDesc.eventSubscribes[i].eventTopic[j]
											+ ")");
							if (event.matches(topicFilter)) {
								switch (bundleDesc.eventSubscribes[i].eventAction[j]) {
									case EventSubscribe.START :
										bundleDesc.applications[i].launch(null);
										break;
									case EventSubscribe.STOP :
									case EventSubscribe.SUSPEND :
									case EventSubscribe.RESUME :				
										
										String pid =(String) bundleDesc.applications[i].getProperties("")
																					 .get( ApplicationDescriptor.APPLICATION_PID );
										
										ServiceReference[] references = bc
												.getServiceReferences(
														"org.osgi.service.application.ApplicationHandle",
														"(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "="
																+ pid + ")");
										if (references == null
												|| references.length == 0)
											break;
										for (int k = 0; k != references.length; k++) {
											MegletHandle handle = (MegletHandle) bc
													.getService(references[k]);
											switch (bundleDesc.eventSubscribes[i].eventAction[j]) {
												case EventSubscribe.STOP :
													handle.destroy();
													break;
												case EventSubscribe.SUSPEND :
													handle.suspend();
													break;
												case EventSubscribe.RESUME :
													handle.resume();
													break;
											}
											bc.ungetService(references[k]);
										}
										break;
								}
							}
						}
				}
			}
		}
		catch (Exception e) {
			log(bc, LogService.LOG_ERROR,
				"Exception occurred at processing a channel event!", e);
		}
	}

	static boolean log(BundleContext bc, int severity, String message, Throwable throwable) {
		System.out.println("Serverity:" + severity + " Message:" + message
				+ " Throwable:" + throwable);

		ServiceReference serviceRef = bc
				.getServiceReference("org.osgi.service.log.LogService");
		if (serviceRef != null) {
			LogService logService = (LogService) bc.getService(serviceRef);
			if (logService != null) {
				try {
					logService.log(severity, message, throwable);
					return true;
				}
				finally {
					bc.ungetService(serviceRef);
				}
			}
		}
		return false;
	}
	
	public ServiceReference getReference( MegletDescriptorImpl megDesc ) {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
			.get(new Long( megDesc.getBundleId() ) );
    if (desc == null)
	    return null;
		
    for( int i=0; i != desc.applications.length; i++ )
      if( megDesc.getMegletDescriptor() == desc.applications[ i ] )
      	return desc.serviceRegistrations[ i ].getReference();
      	
		return null;
	}	
	
	public MegletDescriptor createMegletDescriptorByReflection( Properties props, 
																											Hashtable names, Hashtable icons, 
																				              String defaultLang, String startClass, 
																											String componentName, Bundle bundle ) {
				
		/* That's because of the idiot abstract classes in the API */

		String pid = (String)props.get( ApplicationDescriptor.APPLICATION_PID );
		
		try {
			Class megletDescriptorClass = MegletDescriptor.class;
			Constructor constructor = megletDescriptorClass.getDeclaredConstructor( new Class[] { String.class } );
			constructor.setAccessible( true );
			MegletDescriptor megletDescriptor = (MegletDescriptor) constructor.newInstance( new Object[] { pid } );
			
			Field delegate = megletDescriptorClass.getDeclaredField( "delegate" );
			delegate.setAccessible( true );
			
			MegletDescriptorImpl megDesc = (MegletDescriptorImpl)delegate.get( megletDescriptor );
			
			megDesc.init( bc, props, names, icons, defaultLang, startClass, componentName, bundle, this );
			
			return megletDescriptor;
		}catch( Exception e )
		{
			log(bc, LogService.LOG_ERROR,
					"Exception occurred at creating meglet descriptor!", e);
			return null;
		}		
	}
}

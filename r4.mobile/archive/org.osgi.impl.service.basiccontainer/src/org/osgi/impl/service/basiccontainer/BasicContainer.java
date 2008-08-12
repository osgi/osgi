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
package org.osgi.impl.service.basiccontainer;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;

public class BasicContainer implements ApplicationContainer {
	private BundleContext	bc;
	private String			containerID;
	private Vector			applicationIds;
	private Hashtable		registeredServices;

	public BasicContainer(BundleContext bc, String cntID) throws Exception {
		this.bc = bc;
		this.containerID = cntID;
		loadApplicationIds();
		registeredServices = new Hashtable();
		registerAllApplications();
	}

	public ApplicationDescriptor[] installApplication(InputStream inputStream)
			throws IOException, Exception {
		System.out.println("Install Application called!");
		// getting a unique id for storing the incoming application
		int numId;
		for (numId = 1; applicationIds.contains(Integer.toString(numId)); numId++);
		String id = Integer.toString(numId);
		// writing out the application ID
		applicationIds.add(id);
		saveApplicationIds();
		return download(id, inputStream, null);
	}

	public ApplicationDescriptor[] uninstallApplication(
			ApplicationDescriptor appDescriptor, boolean force)
			throws IOException, Exception {
		BasicContainerDescriptor desc = (BasicContainerDescriptor) appDescriptor;
		if (applicationIds.contains(desc.getId())) {
			unregisterApplication(desc.getId());
			File appDir = bc.getDataFile(desc.getId());
			removeDir(appDir);
			applicationIds.remove(desc.getId());
			saveApplicationIds();
		}
		else
			System.out.println("Can't uninstall application ID ("
					+ desc.getId() + ")!");
		return null;
	}

	public ApplicationDescriptor[] upgradeApplication(
			ApplicationDescriptor appDesc, InputStream inputStream,
			boolean force) throws IOException, Exception {
		String id = ((BasicContainerDescriptor) appDesc).getId();
		unregisterApplication(id);
		if (applicationIds.contains(id)) {
			File appDir = bc.getDataFile(id);
			removeDir(appDir);
			download(id, inputStream, appDesc);
			return null;
		}
		else
			return installApplication(inputStream);
	}

	public Application createApplication(ApplicationContext appContext,
			ApplicationHandle appHandle) throws Exception {
		return new BasicContainerApplication(appContext, appHandle, bc);
	}

	public void registerAllApplications() throws Exception {
		Enumeration elems = applicationIds.elements();
		while (elems.hasMoreElements())
			registerApplication((String) elems.nextElement(), null);
	}

	public void unregisterAllApplications() throws Exception {
		Enumeration elems = applicationIds.elements();
		while (elems.hasMoreElements())
			unregisterApplication((String) elems.nextElement());
	}

	private ApplicationDescriptor[] download(String id,
			InputStream inputStream, ApplicationDescriptor appDesc)
			throws Exception {
		String dirName = id + "/";
		File appDir = bc.getDataFile(dirName);
		appDir.mkdir();
		// extracting the content of the jar file
		ZipInputStream zipStream = new ZipInputStream(inputStream);
		ZipEntry zipEntry;
		while ((zipEntry = zipStream.getNextEntry()) != null) {
			System.out.println("Extracting: " + zipEntry.getName());
			File newFile = bc.getDataFile(dirName + zipEntry.getName());
			if (zipEntry.isDirectory())
				newFile.mkdir();
			else {
				FileOutputStream outStream = new FileOutputStream(newFile);
				BufferedOutputStream bostream = new BufferedOutputStream(
						outStream);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = zipStream.read(buffer, 0, buffer.length)) > 0)
					bostream.write(buffer, 0, length);
				bostream.close();
			}
			zipStream.closeEntry();
		}
		System.out.println();
		zipStream.close();
		return registerApplication(id, appDesc);
	}

	private BasicContainerDescriptor[] registerApplication(String id,
			ApplicationDescriptor appDesc) throws Exception {
		// reading the necessary informations from the manifest file
		File tempFile = bc.getDataFile(id + "/META-INF/MANIFEST.MF");
		FileInputStream manifestStream = new FileInputStream(tempFile);
		Manifest manifest = new Manifest(manifestStream);
		Attributes entries = manifest.getMainAttributes();
		String mainClass = entries.getValue("Main-Class");
		String appCategory = entries.getValue("Application-Category");
		String appName = entries.getValue("Application-Name");
		String appVersion = entries.getValue("Application-Version");
		System.out.println("Registering application: " + appName
				+ "\nCategory: " + appCategory + "\nVersion: " + appVersion
				+ "\nMain Class: " + mainClass);
		// creating the container objects
		BasicContainerDescriptor desc;
		if (appDesc == null)
			desc = new BasicContainerDescriptor(appName, appCategory,
					appVersion, containerID, id, mainClass);
		else {
			desc = (BasicContainerDescriptor) appDesc;
			desc.setName(appName);
			desc.setCategory(appCategory);
			desc.setVersion(appVersion);
		}
		BasicContainerDescriptor[] descArray = new BasicContainerDescriptor[1];
		descArray[0] = desc;
		// registering the service
		Dictionary properties = new Hashtable();
		properties.put("ApplicationName", appName);
		properties.put("ApplicationUID", desc.getUniqueID());
		ServiceRegistration serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationDescriptor", desc,
				properties);
		registeredServices.put(id, serviceReg);
		return descArray;
	}

	private void unregisterApplication(String id) throws Exception {
		Object service;
		if ((service = registeredServices.get(id)) != null) {
			ServiceRegistration sr = (ServiceRegistration) service;
			sr.unregister();
			registeredServices.remove(id);
		}
		else
			System.out.println("Can't unregister application ID (" + id + ")!");
	}

	private void loadApplicationIds() throws Exception {
		applicationIds = new Vector();
		File installedApps = bc.getDataFile("InstalledApplications");
		if (installedApps.exists()) {
			FileInputStream stream = new FileInputStream(installedApps);
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
					System.out.println("Application with id:" + name
							+ " found!");
					applicationIds.add(name.trim());
					index = comma;
				}
			}
		}
	}

	private void saveApplicationIds() throws Exception {
		File installedApps = bc.getDataFile("InstalledApplications");
		FileOutputStream stream = new FileOutputStream(installedApps);
		for (int i = 0; i != applicationIds.size(); i++)
			stream.write((((i == 0) ? "" : ",") + (String) applicationIds
					.get(i)).getBytes());
		stream.close();
	}

	private void removeDir(File file) {
		if (file.isDirectory()) {
			String[] dirs = file.list();
			for (int i = dirs.length - 1; i >= 0; i--)
				removeDir(new File(file, dirs[i]));
		}
		file.delete();
	}
}

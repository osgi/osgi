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
package org.osgi.impl.service.deploymentadmin;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.deploymentadmin.api.*;
import org.osgi.util.tracker.ServiceTracker;

public class DeploymentEngine extends ServiceTracker {
	private DownloadAgentImpl	downloadAgent;
	private BundleContext		context;

	DeploymentEngine(BundleContext context) throws Exception {
		// start monitoring the PackageHandler-s
		super(context, PackageHandler.class.getName(), null);
		open();
		this.context = context;
		downloadAgent = new DownloadAgentImpl(context);
	}

	void destroy() {
		// stop monitoring the PackageHandler-s
		close();
		downloadAgent.destroy();
	}

	// finds the appropriate PackageHandler for the type
	private PackageHandler getHandler(String type) {
		PackageHandler ret = null;
		ServiceReference[] refs = getServiceReferences();
		if (null == refs)
			return null;
		for (int i = 0; i < refs.length; ++i) {
			String prop = (String) refs[i]
					.getProperty(PackageHandler.PACKAGETYPE);
			if (prop.indexOf(type) > -1) {
				ret = (PackageHandler) getService(refs[i]);
				break;
			}
		}
		return ret;
	}

	void install(URL url) throws PackageHandlerException {
		DownloadInputStream is = null;
		try {
			is = downloadAgent.download(url);
		}
		catch (DownloadException e) {
			e.printStackTrace();
			throw new PackageHandlerException(e.getMessage());
		}
		// if there is descriptor file
		if ("text/plain".equals(is.getDescriptor().get(
				DownloadInputStream.MIMETYPE))) {
			String packageType = null;
			String appURL = null;
			Map data = null;
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is));
				packageType = br.readLine();
				appURL = br.readLine();
				// read additional data from the descriptor file
				data = readData(br);
			}
			catch (IOException e) {
				throw new PackageHandlerException(e.getMessage());
			}
			finally {
				if (null != br)
					try {
						br.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
			}
			try {
				installApp(new URL(appURL), packageType, data);
			}
			catch (Exception e) {
				throw new PackageHandlerException(e.getMessage());
			}
			// if there is not descriptor file than the default type is suite
		}
		else {
			try {
				PackageHandler handler = getHandler(PackageHandler.PACKAGETYPE_SUITE);
				if (null == handler)
					throw new PackageHandlerException(
							"There is no appropriate " + "package handler for "
									+ PackageHandler.PACKAGETYPE_SUITE);
				handler.install(is, PackageHandler.PACKAGETYPE_SUITE, null);
			}
			finally {
				if (null != is)
					try {
						is.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	private Map readData(BufferedReader br) throws IOException {
		Map ret = new HashMap();
		String line = br.readLine();
		while (null != line && !"".equals(line.trim())) {
			int e = line.indexOf("=");
			String key = line.substring(0, e);
			String value = line.substring(e + 1);
			ret.put(key, value);
			line = br.readLine();
		}
		return ret;
	}

	private void installApp(URL url, String packageType, Map data)
			throws DownloadException, PackageHandlerException {
		InputStream is = downloadAgent.download(url);
		try {
			PackageHandler handler = getHandler(packageType);
			if (null == handler)
				throw new PackageHandlerException("There is no appropriate "
						+ "package handler for " + packageType);
			handler.install(is, packageType, data);
		}
		finally {
			try {
				if (null != is)
					is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

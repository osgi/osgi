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
package org.osgi.meg.demo.desktop;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.meg.demo.util.Splitter;
import org.osgi.service.application.*;
import org.osgi.service.dmt.*;
import org.osgi.service.event.*;
import org.osgi.service.log.*;
import org.osgi.util.tracker.*;

/**
 * Model part of the MVC pattern.
 */
public class Model extends ServiceTracker implements Runnable, ChannelListener {
	private Desktop			desktop;
	private BundleContext	context;
	private ServiceTracker	trackAppHandle;
	private ServiceTracker	trackAppDescr;
	private boolean			running;
	private Hashtable		events	= new Hashtable();
	// TODO eliminate this
	private DmtExecPlugIn	execPlugin;

	Model(BundleContext context, Desktop desktop) throws Exception {
		// track the Application Manager
		super(context, ApplicationManager.class.getName(), null);
		this.context = context;
		this.desktop = desktop;
		// for the anonymous inner classes
		final BundleContext constcontext = context;
		final Desktop constdesktop = desktop;
		// track ApplicationHandle
		trackAppHandle = new ServiceTracker(context, ApplicationHandle.class
				.getName(), new ServiceTrackerCustomizer() {
			public Object addingService(ServiceReference reference) {
				ApplicationHandle ret = (ApplicationHandle) constcontext
						.getService(reference);
				constdesktop.addLaunchApp(ret);
				return ret;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
			}

			public void removedService(ServiceReference reference,
					Object service) {
				constdesktop.removeRunApp((ApplicationHandle) service);
				constcontext.ungetService(reference);
			}
		});
		trackAppHandle.open();
		// track ApplicationDescriptor
		final BundleContext finalContext = context;
		trackAppDescr = new ServiceTracker(context, ApplicationDescriptor.class
				.getName(), new ServiceTrackerCustomizer() {
			public Object addingService(ServiceReference reference) {
				ApplicationDescriptor descr = (ApplicationDescriptor) constcontext
						.getService(reference);
				String uid = (String) reference.getProperty("unique_id");
				String appName = (String) reference
						.getProperty("localized_name");
				byte[] imageData = createImageData(reference, finalContext);
				constdesktop.addInstApp(descr, appName, imageData);
				return descr;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
			}

			public void removedService(ServiceReference reference,
					Object service) {
				String uid = (String) reference.getProperty("unique_id");
				constdesktop.removeInstApp((ApplicationDescriptor) service);
				constcontext.ungetService(reference);
			}
		});
		trackAppDescr.open();
		// TODO eliminate this
		//ServiceReference sref = context.getServiceReference(DmtExecPlugIn.class
		//		.getName());
		//execPlugin = (DmtExecPlugIn) context.getService(sref);
		System.out.println("Ignored, not sure what to do here with an exec plugin?");
		
		// register ChannelListener
		Hashtable config = new Hashtable();
		config.put("topic", "*");
		context.registerService(ChannelListener.class.getName(), this, config);
	}

	private static byte[] createImageData(ServiceReference reference,
			BundleContext context) {
		InputStream iconStream = null;
		// TODO not only 8x8
		String iconPath = (String) reference
				.getProperty("localized_icon_8x8_path");
		Bundle bundle = null;
		URL url = null;
		byte[] imageData = null;
		if (iconPath.startsWith("bundle://")) {
			String[] sarr = Splitter.split(iconPath, '/', 0);
			String fileName = sarr[sarr.length - 1];
			sarr = Splitter.split(sarr[2], ':', 0);
			bundle = context.getBundle(Long.parseLong(sarr[0]));
			url = bundle.getResource(fileName);
		}
		else {
			bundle = reference.getBundle();
			url = bundle.getResource(iconPath);
		}
		if (null != url) {
			try {
				iconStream = url.openConnection().getInputStream();
				imageData = createImageArray(iconStream);
				;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (null != iconStream) {
					try {
						iconStream.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		return imageData;
	}

	private static byte[] createImageArray(InputStream iconStream) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int d = iconStream.read();
			while (d != -1) {
				bos.write(d);
				d = iconStream.read();
			}
			return bos.toByteArray();
		}
		catch (IOException e) {
			return null;
		}
		finally {
			if (null != bos) {
				try {
					bos.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	// closes the resources. called by the DesktopActivator
	public void destroy() {
		synchronized (this) {
			running = false;
		}
		close();
		trackAppHandle.close();
		trackAppDescr.close();
	}

	public void launchApp(ApplicationDescriptor descr) throws Exception {
		ApplicationHandle handle = ((ApplicationManager) getService())
				.launchApplication(descr, new HashMap());
	}

	public void installApp(String url) throws DmtException {
		// TODO eliminate this
		//execPlugin.execute(null, "./OSGi/deploy/install", url);
		System.out.println("Ignored, not sure what to do here with an exec plugin?");
	}

	public void uninstallApp(ApplicationDescriptor descr) throws Exception {
		String type = descr.getContainerID();
		ServiceReference[] refs = context.getServiceReferences(
				ApplicationContainer.class.getName(), "(application_type=MEG)");
		ApplicationContainer cont = (ApplicationContainer) context
				.getService(refs[0]);
		cont.uninstallApplication(descr, false);
		context.ungetService(refs[0]);
	}

	public void stopApp(ApplicationHandle handle) throws Exception {
		handle.destroyApplication();
	}

	public void suspendApp(ApplicationHandle handle) throws Exception {
		handle.suspendApplication();
	}

	public void resumeApp(ApplicationHandle handle) throws Exception {
		handle.resumeApplication();
	}

	public void run() {
		running = true;
		while (running) {
			synchronized (this) {
				try {
					ServiceReference[] refs = context.getServiceReferences(
							ApplicationHandle.class.getName(), null);
					if (null != refs) {
						Hashtable ht = new Hashtable();
						for (int i = 0; i < refs.length; ++i) {
							ApplicationHandle handle = (ApplicationHandle) context
									.getService(refs[i]);
							ht.put(handle, new Integer(handle.getAppStatus()));
							desktop.refreshRunningApps(ht);
							context.ungetService(refs[i]);
						}
					}
				}
				catch (InvalidSyntaxException e) {
					throw new RuntimeException("Internal error.");
				}
			}
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
			}
		}
	}

	public String getLogs() {
		ServiceReference sref = context
				.getServiceReference(LogReaderService.class.getName());
		LogReaderService lr = (LogReaderService) context.getService(sref);
		StringBuffer sb = new StringBuffer();
		String[] level = new String[] {"ERROR", "WARNING", "INFO", "DEBUG"};
		for (Enumeration en = lr.getLog(); en.hasMoreElements();) {
			LogEntry le = (LogEntry) en.nextElement();
			Date d = new Date(le.getTime());
			sb.append(d + "    " + level[le.getLevel() - 1] + " \t"
					+ le.getMessage() + "\n");
		}
		context.ungetService(sref);
		return sb.toString();
	}

	public void channelEvent(ChannelEvent event) {
		// TODO
		if (!"Bundle Event".equals(event.getTopic()))
			return;
		Object[] pair = (Object[]) events.get(event.getTopic());
		if (null == pair)
			return;
		Hashtable props = (Hashtable) pair[0];
		ApplicationDescriptor descr = (ApplicationDescriptor) pair[1];
		long ide = ((Long) event.getProperty("bundle.id")).longValue();
		long idp = ((Long) props.get("bundle.id")).longValue();
		if (ide == idp) {
			try {
				((ApplicationManager) getService()).launchApplication(
						(ApplicationDescriptor) pair[1], null);
			}
			catch (SingletonException e) {
				// TODO
				e.printStackTrace();
			}
			catch (Exception e) {
				// TODO
				e.printStackTrace();
			}
			events.remove(event.getTopic());
		}
	}

	public void scheduleOnEvent(String topic, Hashtable ht,
			ApplicationDescriptor descr) {
		events.put(topic, new Object[] {ht, descr});
	}

	public void scheduleOnDate(Date date, ApplicationDescriptor descr) {
		((ApplicationManager) getService()).addScheduledApplication(descr,
				new Hashtable(), date);
	}
}

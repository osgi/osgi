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
import java.lang.reflect.Method;
import java.util.Iterator;
import org.osgi.framework.BundleContext;
import org.osgi.service.application.*;

class FileClassLoader extends ClassLoader {
	File	root;

	FileClassLoader(File rootDir) {
		root = rootDir;
	}

	public Class findClass(String name) {
		byte[] b = loadClassData(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(String nameIn) {
		try {
			String name = nameIn.replace('.', '/');
			File classFile = new File(root, name + ".class");
			byte[] b = new byte[(int) classFile.length()];
			FileInputStream loaderStream = new FileInputStream(classFile);
			int received = 0;
			while (received < b.length) {
				received += loaderStream.read(b, received, b.length);
			}
			loaderStream.close();
			return b;
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return null;
		}
	}
}

class BasicApplicationThread extends Thread {
	private ApplicationHandle	appHandle;
	private BundleContext		bc;
	private ApplicationContext	appContext;

	public BasicApplicationThread(ApplicationHandle appHandle,
			BundleContext bc, ApplicationContext appContext) {
		this.appHandle = appHandle;
		this.bc = bc;
		this.appContext = appContext;
	}

	public void run() {
		try {
			BasicContainerDescriptor desc = (BasicContainerDescriptor) (appHandle
					.getAppDescriptor());
			ClassLoader loader = new FileClassLoader(bc.getDataFile(desc
					.getId()));
			Class main = loader.loadClass(desc.getMainClass());
			String args[] = new String[appContext.getLaunchArgs().size() * 2];
			Iterator keys = appContext.getLaunchArgs().keySet().iterator();
			for (int i = 0; keys.hasNext(); i += 2) {
				args[i] = keys.next().toString();
				args[i + 1] = appContext.getLaunchArgs().get(args[i])
						.toString();
			}
			Method mainMethod = main.getMethod("main", new Class[] {args
					.getClass()});
			mainMethod.invoke(null, new Object[] {args});
			appHandle.destroyApplication();
		}
		catch (Exception e) {
			System.out.println("Exception received: " + e);
			e.printStackTrace();
		}
	}
}

public class BasicContainerApplication implements Application {
	private ApplicationHandle	appHandle;
	private ApplicationContext	appContext;
	private BundleContext		bc;

	BasicContainerApplication(ApplicationContext ctx, ApplicationHandle handle,
			BundleContext bc) {
		appContext = ctx;
		appHandle = handle;
		this.bc = bc;
	}

	public void startApplication() throws Exception {
		System.out.println("Application.startApplication() called!");
		BasicApplicationThread appThread = new BasicApplicationThread(
				appHandle, bc, appContext);
		appThread.start();
	}

	public void stopApplication() throws Exception {
		/* TODO  */
	}

	public void suspendApplication() throws Exception {
		/* TODO  */
	}

	public void resumeApplication() throws Exception {
		/* TODO  */
	}
}

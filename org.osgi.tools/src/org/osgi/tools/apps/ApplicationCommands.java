/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2006). All Rights Reserved.
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

package org.osgi.tools.apps;

import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.tools.command.*;
import org.osgi.util.tracker.ServiceTracker;

public class ApplicationCommands implements CommandProvider, BundleActivator {
	ApplicationDescriptor	p;
	BundleContext			context;
	MapTracker				descriptors;
	MapTracker				handles;

	class MapTracker extends ServiceTracker {
		Map	map	= new TreeMap();

		MapTracker(BundleContext context, Class clazz) {
			super(context, clazz.getName(), null);
		}

		public Object addingService(ServiceReference ref) {
			map.put(ref.getProperty("service.pid"), ref);
			return ref;
		}
		
		public void removedService( ServiceReference ref, Object service ) {
			map.remove(ref.getProperty("service.pid"));	
		}
	};

	public void start(BundleContext context) {
		this.context = context;
		descriptors = new MapTracker(context, ApplicationDescriptor.class);
		handles = new MapTracker(context, ApplicationHandle.class);
		descriptors.open();
		handles.open();
		context.registerService(CommandProvider.class.getName(), this, null);
	}

	public void stop(BundleContext context) {
		this.context = null;
	}

	public String getHelp() {
		return "APPLICATION COMMANDS\r\n"
				+ "apps [descriptors|handles|launch <name> [key=value]*]|kill <id>|props <name>]\r\n";
	}

	public Object toString(Object appDescriptor) {
		if (appDescriptor instanceof ApplicationDescriptor) {
			ApplicationDescriptor appd = (ApplicationDescriptor) appDescriptor;
			StringBuffer sb = new StringBuffer();
			sb.append(appd.getApplicationId());
			sb.append(" - ");
			sb.append(appd.getProperties(null));
			return sb.toString();
		}
		return null;
	}

	public Object _apps(CommandInterpreter intp) throws Exception {
		String cmd = intp.nextArgument();
		while (cmd != null) {
			if ("descriptors".equals(cmd))
				return descriptors.map.keySet();
			else if ("handles".equals(cmd))
				return handles.map.keySet();
			else if ("launch".equals(cmd))
				return launch(intp);
			else if ("kill".equals(cmd))
				return kill(intp);
			else if ("props".equals(cmd))
				return props(intp);
			else
				throw new IllegalArgumentException("Unknown subcmd: " + cmd);
		}
		return null;
	}

	private Object props(CommandInterpreter intp) {
		String item = intp.nextArgument();
		if (item == null)
			throw new IllegalArgumentException("No descriptor given");

		ServiceReference ref = (ServiceReference) descriptors.map.get(item);
		if (ref == null)
			throw new IllegalArgumentException("No such descriptor " + item);

		ApplicationDescriptor appd = (ApplicationDescriptor) context
				.getService(ref);
		if (appd == null)
			throw new IllegalArgumentException("Descriptor " + item + " gone ");
		return appd.getProperties(null);

	}

	private Object kill(CommandInterpreter intp) {
		String item = intp.nextArgument();
		if (item == null)
			throw new IllegalArgumentException("No handle id given");

		ServiceReference ref = (ServiceReference) handles.map.get(item);
		if (ref == null)
			throw new IllegalArgumentException("No such handle " + item);

		ApplicationHandle handle = (ApplicationHandle) context.getService(ref);

		if (handle == null)
			throw new IllegalArgumentException("Handle gone " + item);

		handle.destroy();

		return handle;
	}

	private Object launch(CommandInterpreter intp) throws ApplicationException {
		String item = intp.nextArgument();
		if (item == null)
			throw new IllegalArgumentException("No descriptor given");

		Map map = getMap(intp);
		ServiceReference ref = (ServiceReference) descriptors.map.get(item);
		if (ref == null)
			throw new IllegalArgumentException("No such descriptor " + item);
		ApplicationDescriptor appd = (ApplicationDescriptor) context
				.getService(ref);
		if (appd == null)
			throw new IllegalArgumentException("Descriptor " + item + " gone ");
		return appd.launch(map);
	}

	private Map getMap(CommandInterpreter intp) {
		Map map = new TreeMap();

		while (true) {
			String key = intp.nextArgument();
			if (key == null)
				break;
			String op = intp.nextArgument();
			String value = intp.nextArgument();
			if (key == null || value == null || op == null || !"=".equals(op))
				throw new IllegalArgumentException("Invalid map definition: "
						+ key + op + value);

			map.put(key, value);
		}
		return map;
	}
}
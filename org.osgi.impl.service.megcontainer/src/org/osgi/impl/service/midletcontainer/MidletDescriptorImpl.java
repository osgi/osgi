package org.osgi.impl.service.midletcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import javax.microedition.midlet.MIDlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.midlet.MidletDescriptor;
import org.osgi.service.application.midlet.MidletHandle;

public final class MidletDescriptorImpl implements
		org.osgi.service.application.midlet.MidletDescriptor.Delegate {
	private Properties			props;
	private Hashtable			names;
	private Hashtable			icons;
	private BundleContext		bc;
	private String				startClass;
	private String				pid;
	private Bundle				bundle;
	private String				defaultLanguage;
	private boolean				locked;
	private MidletDescriptor	midletDescriptor;
	private MidletContainer		midletContainer;
	private static int			instanceCounter;

	public MidletDescriptorImpl() {}

	public void init(BundleContext bc, Properties props, Map names, Map icons,
			String defaultLang, String startClass, Bundle bundle,
			MidletContainer midletContainer) throws Exception {
		this.bc = bc;
		this.midletContainer = midletContainer;
		this.props = new Properties();
		this.props.putAll(props);
		this.names = new Hashtable(names);
		this.icons = new Hashtable(icons);
		this.startClass = startClass;
		this.bundle = bundle;
		if (names.size() == 0 || icons.size() == 0
				|| !props.containsKey("application.bundle.id")
				|| !props.containsKey("service.pid")
				|| !props.containsKey("application.version"))
			throw new Exception("Invalid MEG container input!");
		if (!names.containsKey(defaultLang)) {
			throw new Exception("Invalid default language!");
		}
		else {
			defaultLanguage = defaultLang;
			pid = props.getProperty("service.pid");
			return;
		}
	}

	public long getBundleId() {
		return Long.parseLong(props.getProperty("application.bundle.id"));
	}

	Bundle getBundle() {
		return bundle;
	}

	public String getStartClass() {
		return startClass;
	}

	public String getPID() {
		return pid;
	}

	protected BundleContext getBundleContext() {
		return bc;
	}

	public Map getPropertiesSpecific(String locale) {
		Hashtable properties = new Hashtable();
		String localizedName = (String) names.get(locale);
		if (localizedName == null) {
			if ((localizedName = (String) names.get(defaultLanguage)) == null) {
				Enumeration enum = names.keys();
				String firstKey = (String) enum.nextElement();
				localizedName = (String) names.get(firstKey);
				locale = firstKey;
			}
			else {
				localizedName = (String) names.get(defaultLanguage);
			}
			locale = defaultLanguage;
		}
		properties.put("application.name", localizedName);
		properties.put("application.icon", icons.get(locale));
		properties.put("application.bundle.id", props
				.getProperty("application.bundle.id"));
		properties.put("application.version", props
				.getProperty("application.version"));
		properties.put("application.vendor", props
				.getProperty("application.vendor"));
		String visible = props.getProperty("application.visible");
		if (visible != null && visible.equalsIgnoreCase("false"))
			properties.put("application.visible", "false");
		else
			properties.put("application.visible", "true");
		boolean launchable = false;
		try {
			launchable = midletContainer.isLaunchable(this);
		}
		catch (Exception e) {
			MidletContainer
					.log(
							bc,
							1,
							"Exception occurred at searching the Midlet container reference!",
							e);
		}
		properties.put("application.locked", (new Boolean(locked)).toString());
		properties.put("application.launchable", (new Boolean(launchable))
				.toString());
		properties.put("application.type", "MIDlet");
		properties.put("service.pid", new String(pid));
		return properties;
	}

	public ApplicationHandle launchSpecific(Map args) throws Exception {
		Vector refs = new Vector();
		MIDlet midlet = midletContainer.createMidletInstance(this, refs, false);
		if (midlet == null)
			throw new Exception("Cannot create meglet instance!");
		else
			return createMidletHandleByReflection(midlet, args, refs, midlet);
	}

	public void lockSpecific() {
		locked = true;
	}

	public void unlockSpecific() {
		locked = false;
	}

	public boolean isLocked() {
		return locked;
	}

	public MidletDescriptor getMidletDescriptor() {
		return midletDescriptor;
	}

	static synchronized String createNewInstanceID(String pid) {
		return new String(pid + ":" + instanceCounter++);
	}

	public MidletHandle createMidletHandleByReflection(MIDlet midlet, Map args,
			Vector refs, Object baseClass) {
		try {
			Class midletHandleClass = org.osgi.service.application.midlet.MidletHandle.class;
			Constructor constructor = midletHandleClass
					.getDeclaredConstructor(new Class[] {
							java.lang.String.class,
							org.osgi.service.application.midlet.MidletDescriptor.class});
			constructor.setAccessible(true);
			MidletHandle midletHandle = (MidletHandle) constructor
					.newInstance(new Object[] {createNewInstanceID(pid),
							midletDescriptor});
			Field delegate = midletHandleClass.getDeclaredField("delegate");
			delegate.setAccessible(true);
			MidletHandleImpl midHnd = (MidletHandleImpl) delegate
					.get(midletHandle);
			midHnd.init(bc, midletContainer, midlet, refs, baseClass);
			midHnd.startHandle(args);
			return midletHandle;
		}
		catch (Exception e) {
			MidletContainer.log(bc, 1,
					"Exception occurred at creating midlet handle!", e);
		}
		return null;
	}

	public void setMidletDescriptor(MidletDescriptor descriptor) {
		midletDescriptor = descriptor;
	}
}
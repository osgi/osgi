package org.osgi.service.application.midlet;

import java.util.Map;

import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;

public final class MidletDescriptor extends ApplicationDescriptor {

	protected ApplicationHandle launchSpecific(Map arguments) throws Exception {
		return delegate.launchSpecific( arguments );
	}

	protected Map getPropertiesSpecific(String locale) {
		return delegate.getPropertiesSpecific( locale );
	}

	protected void lockSpecific() {
		delegate.lockSpecific();
	}

	protected void unlockSpecific() {
		delegate.unlockSpecific();
	}
	
	protected  MidletDescriptor(String pid) {
		super( pid );

		try {
			delegate = (Delegate) implementation
					.newInstance();
			delegate.setMidletDescriptor( this );
		}
		catch (Exception e) {
			// Too bad ...
			e.printStackTrace();
			System.err
					.println("No implementation available for ApplicationDescriptor, property is: "
							+ cName);
		}
	}

	Delegate	delegate;
	String							pid;

	static Class					implementation;
	static String					cName;

	static
	{
		try {
			cName = System
					.getProperty("org.osgi.vendor.application.midlet.MidletDescriptor");
			implementation = Class.forName(cName);
		}
		catch (Throwable t) {
			// Ignore
		}
	}

	public interface Delegate {
		void setMidletDescriptor( MidletDescriptor descriptor );
		ApplicationHandle launchSpecific(Map arguments) throws Exception;
		Map getPropertiesSpecific(String locale);
		void lockSpecific();
		void unlockSpecific();
	}
}

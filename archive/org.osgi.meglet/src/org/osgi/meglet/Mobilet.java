package org.osgi.meglet;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.service.application.Application;
import org.osgi.service.component.ComponentContext;

/**
 * A MEG Application. a.k.a Meglet
 */

public class Mobilet implements Application {

	ComponentContext context;
	Map args;

	void activate(ComponentContext context) {
		this.context = context;
	}

	void deactivate(ComponentContext context) {
		this.context = null;
	}

	public void startApplication(Map args) throws Exception {
		this.args = args;
		start();
	}

	public void stopApplication() throws Exception {
		try {
			stop();
		} catch (Exception e) {
		}
		args = null;
		context = null;
	}

	public void suspendApplication() throws Exception {
		suspend();
	}

	public void resumeApplication() throws Exception {
		resume();
	}

	protected Dictionary getProperties() {
		return context.getProperties();
	}

	protected Object locateService(String name) {
		return context.locateService(name);
	}
	protected Object[] locateServices(String name) {
		return context.locateServices(name);
	}

	protected void suspend() throws Exception {
	}
	protected void resume()  throws Exception {
	}
	protected void start()  throws Exception {
	}
	protected void stop()  throws Exception {
	}

	protected void requestStop() {
		context.disableComponent(null);
	}
}


package org.osgi.service.application;

import org.osgi.service.event.EventChannel;

public interface MEGApplicationContext extends ApplicationContext {

	public Object getServiceObject(String className, String filter) throws Exception;

	public Object getServiceObject(String className, String filter, long millisecs) throws Exception;

	public boolean ungetServiceObject(Object serviceObject);

	public EventChannel getEventChannel();

	public ApplicationManager getApplicationManager();

}

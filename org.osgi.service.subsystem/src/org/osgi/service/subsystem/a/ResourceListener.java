package org.osgi.service.subsystem.a;

public interface ResourceListener {
	public void installed(Resource resource);
	
	public void installing(Resource resource);
	
	public void started(Resource resource);
	
	public void starting(Resource resource);
	
	public void stopped(Resource resource);
	
	public void uninstalled(Resource resource);
	
	public void uninstalling(Resource resource);
	
	public void updated(Resource resource);
	
	public void updating(Resource resource);
}

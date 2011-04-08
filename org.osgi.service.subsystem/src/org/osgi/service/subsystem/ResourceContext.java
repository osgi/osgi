package org.osgi.service.subsystem;

import org.osgi.service.coordinator.Coordination;

public interface ResourceContext {
	public Coordination getCoordination();
	
	public Resource getPreviousResource();
	
	public Resource getResource();
	
	public Subsystem getSubsystem();
}

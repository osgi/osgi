package org.osgi.service.deployment;

public interface Resource {
	String getName();

	void deploy();

	void remove();
}

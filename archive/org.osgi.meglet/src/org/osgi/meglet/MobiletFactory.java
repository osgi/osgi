package org.osgi.meglet;

import java.util.*;

import org.osgi.service.component.*;

public class MobiletFactory implements ComponentFactory {
	ComponentFactory	factory;
	
	public MobiletFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public ComponentInstance newInstance(Dictionary properties) {
		return factory.newInstance(properties);
	}
	
}

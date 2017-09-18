package org.osgi.test.cases.jaxrs.applications;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class SimpleApplication extends Application {

	private final Set<Class< ? >>	classes		= new HashSet<>();

	private final Set<Object>		singletons	= new HashSet<>();

	public SimpleApplication(Set<Class< ? >> classes, Set<Object> singletons) {
		this.classes.addAll(classes);
		this.singletons.addAll(singletons);
	}

	@Override
	public Set<Class< ? >> getClasses() {
		return new HashSet<>(classes);
	}

	@Override
	public Set<Object> getSingletons() {
		return new HashSet<>(singletons);
	}

}

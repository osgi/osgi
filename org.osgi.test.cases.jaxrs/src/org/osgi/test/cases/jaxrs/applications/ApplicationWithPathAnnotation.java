package org.osgi.test.cases.jaxrs.applications;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("annotated")
public class ApplicationWithPathAnnotation extends SimpleApplication {

	public ApplicationWithPathAnnotation(Set<Class< ? >> classes,
			Set<Object> singletons) {
		super(classes, singletons);
	}

}

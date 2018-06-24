package org.osgi.impl.bundle.annotations.reqcap;

import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Requirement;

@Requirement(namespace = "testDirectlyAnnotatedDefaultOptions")
// We need the following to enable the requirement above to resolve.
@Capability(namespace = "testDirectlyAnnotatedDefaultOptions")
public @interface DefaultOptionsAnnotation {
	//
}

package org.osgi.impl.bundle.annotations.reqcap;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Directive;
import org.osgi.annotation.bundle.Requirement;

@Requirement(namespace = "testDirectlyAnnotatedAttributes")
@Capability(namespace = "testDirectlyAnnotatedAttributes")
public @interface AttributesAnnotation {
	@Attribute("attr")
	String value();

	@Directive("x-directive")
	String directive();

	@Attribute
	long longAttr();

	@Attribute
	String stringAttr();

	@Attribute
	double doubleAttr();

	@Attribute
	long[] longList();

	@Attribute
	String[] stringList();

	@Attribute
	double[] doubleList();
}

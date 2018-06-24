package org.osgi.impl.bundle.annotations.reqcap;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Directive;
import org.osgi.annotation.bundle.Requirement;

@Requirement(namespace = "testDirectlyAnnotatedAttributesDefaults")
@Capability(namespace = "testDirectlyAnnotatedAttributesDefaults")
public @interface AttributesDefaultsAnnotation {
	@Attribute("attr")
	String value() default "";

	@Directive("x-directive")
	String directive() default "";

	@Attribute
	long longAttr() default 0;

	@Attribute
	String stringAttr() default "";

	@Attribute
	double doubleAttr() default 0;

	@Attribute
	long[] longList() default {};

	@Attribute
	String[] stringList() default {};

	@Attribute
	double[] doubleList() default {};
}

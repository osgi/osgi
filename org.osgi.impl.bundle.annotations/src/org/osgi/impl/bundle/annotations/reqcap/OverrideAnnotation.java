package org.osgi.impl.bundle.annotations.reqcap;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Directive;
import org.osgi.annotation.bundle.Requirement;

@ThirdAnnotation(foo = "foobar3")
public @interface OverrideAnnotation {
	//
}

@Capability(namespace = "testOverriding", name = "override", version = "1.5")
@Requirement(namespace = "testOverriding", name = "override", version = "1.5")
@interface FirstAnnotation {

	// Not an override as this is an attribute
	@Attribute
	String name();

	@Attribute
	String foo();

	@Directive("x-top-name")
	String fizz();
}

@FirstAnnotation(name = "First", foo = "bar1", fizz = "buzz1")
@interface SecondAnnotation {
	// Not an override as this is a directive
	@Directive("x-name")
	String name();

	// Override, even though the member name is different
	@Directive("x-top-name")
	String middle();
}

@SecondAnnotation(name = "Second", middle = "fizzbuzz2")
@interface ThirdAnnotation {
	// Overrides and changes type
	@Attribute
	String[] foo();
}

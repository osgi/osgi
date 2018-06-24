package org.osgi.impl.bundle.annotations.reqcap;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.annotation.bundle.Requirement.Cardinality;
import org.osgi.annotation.bundle.Requirement.Resolution;

@Requirement( //
		namespace = "testDirectlyAnnotatedRequirement", //
		name = "allOptions", //
		version = "1.3", //
		filter = "(foo=bar)", //
		effective = "osgi.test", //
		attribute = {
				"attr=value", //
				"x-directive:=directiveValue", //
				"longAttr:Long=42", //
				"stringAttr:String=stringValue", //
				"doubleAttr:Double=4.2", //
				"versionAttr:Version=4.2", //
				"longList:List<Long>='2,3,4'", //
				"stringList:List<String>='one,two,three'", //
				"doubleList:List<Double>='2.3,3.4,4.5'", //
				"versionList:List<Version>='2.3,3.4,4.5'" //
		}, //
		resolution = Resolution.OPTIONAL, //
		cardinality = Cardinality.MULTIPLE //
)
public @interface DirectRequirementAnnotation {
	//
}

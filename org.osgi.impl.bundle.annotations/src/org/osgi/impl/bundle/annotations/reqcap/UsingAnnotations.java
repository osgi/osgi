package org.osgi.impl.bundle.annotations.reqcap;

@DirectRequirementAnnotation
@IndirectRequirementAnnotation
@DefaultOptionsAnnotation
@AttributesAnnotation(//
		value = "value", //
		directive = "directiveValue", //
		longAttr = 42, //
		stringAttr = "stringValue", //
		doubleAttr = 4.2, //
		longList = {
				2, 3, 4
		}, //
		stringList = {
				"one", "two", "three"
		}, //
		doubleList = {
				2.3, 3.4, 4.5
		} //
)
@AttributesDefaultsAnnotation
@DirectCapabilityAnnotation
@IndirectCapabilityAnnotation
@OverrideAnnotation
public class UsingAnnotations {
	//
}

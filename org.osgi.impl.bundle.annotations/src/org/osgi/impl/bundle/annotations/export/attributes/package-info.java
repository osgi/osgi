@Version("1.0")
@Export(attribute = {
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
})
package org.osgi.impl.bundle.annotations.export.attributes;

import org.osgi.annotation.bundle.Export;
import org.osgi.annotation.versioning.Version;

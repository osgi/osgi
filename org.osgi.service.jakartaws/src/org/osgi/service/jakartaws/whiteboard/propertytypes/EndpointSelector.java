package org.osgi.service.jakartaws.whiteboard.propertytypes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.jakartaws.whiteboard.SoapWhiteboardConstants;
import org.osgi.service.jakartaws.whiteboard.annotations.RequireSoapWhiteboard;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE
})
@RequireSoapWhiteboard
@ComponentPropertyType
public @interface EndpointSelector {
	
	String PREFIX_ = SoapWhiteboardConstants.SOAP_PREFIX;
	
	String value();

}

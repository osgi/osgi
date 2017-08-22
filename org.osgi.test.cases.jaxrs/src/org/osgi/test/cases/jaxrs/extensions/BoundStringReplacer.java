package org.osgi.test.cases.jaxrs.extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

import org.osgi.test.cases.jaxrs.extensions.BoundStringReplacer.NameBound;

@NameBound
public class BoundStringReplacer extends StringReplacer {

	@Target({
			ElementType.TYPE, ElementType.METHOD
	})
	@Retention(RetentionPolicy.RUNTIME)
	@NameBinding
	public @interface NameBound {}

	public BoundStringReplacer(String toReplace, String replaceWith) {
		super(toReplace, replaceWith);
	}
}

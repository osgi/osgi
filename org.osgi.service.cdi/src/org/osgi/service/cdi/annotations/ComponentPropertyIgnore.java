
package org.osgi.service.cdi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * A meta annotation that can be placed on {@link Qualifier} annotations to
 * exclude them from being used as service properties.
 * <p>
 * This annotation is processed only to check the qualifiers used together with
 * {@link Service} and {@link Reference}.
 * 
 * @author $ID$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ComponentPropertyIgnore {
	/**
	 * Support inline instantiation of the {@link ComponentPropertyIgnore}
	 * annotation.
	 */
	public static final class Literal extends AnnotationLiteral<ComponentPropertyIgnore> implements ComponentPropertyIgnore {
		/**
		 * Default instance
		 */
		public static final ComponentPropertyIgnore	INSTANCE			= new Literal();

		private static final long					serialVersionUID	= 1L;
	}
}

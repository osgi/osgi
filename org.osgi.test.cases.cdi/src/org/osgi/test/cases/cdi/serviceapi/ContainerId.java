package org.osgi.test.cases.cdi.serviceapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

@Qualifier
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER })
public @interface ContainerId {

	/**
	 * Support inline instantiation of the {@link ContainerId} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<ContainerId> implements ContainerId {

		private static final long serialVersionUID = 1L;

		/**
		 * @param id
		 * @return an instance of {@link ContainerId}
		 */
		public static final Literal of(String id) {
			return new Literal(id);
		}

		private Literal(String id) {
			_id = id;
		}

		@Override
		public String value() {
			return _id;
		}

		private final String _id;

	}

	String value();

}

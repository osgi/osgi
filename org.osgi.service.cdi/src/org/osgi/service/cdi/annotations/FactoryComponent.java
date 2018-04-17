/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.cdi.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

import org.osgi.service.cdi.CDIConstants;

/**
 * Identifies a factory component.
 * <p>
 * Factory components MUST always be {@link ComponentScoped ComponentScoped}.
 * Applying any other scope will result in a definition error.
 *
 * @author $Id$
 * @see "Factory Component"
 */
@ComponentScoped
@Documented
@Named
@Retention(RUNTIME)
@Stereotype
@Target(TYPE)
public @interface FactoryComponent {

	/**
	 * Support inline instantiation of the {@link FactoryComponent} annotation.
	 */
	public static final class Literal extends
			AnnotationLiteral<FactoryComponent> implements FactoryComponent {

		private static final long serialVersionUID = 1L;

		/**
		 * @param pid the factory configuration pid
		 * @return an instance of {@link FactoryComponent}
		 */
		public static final Literal of(String pid) {
			return new Literal(pid);
		}

		private Literal(String pid) {
			_pid = pid;
		}

		@Override
		public String value() {
			return _pid;
		}

		private final String _pid;

	}

	/**
	 * The configuration PID for the configuration of this Component.
	 *
	 * <p>
	 * The value specifies a configuration PID who's configuration properties are
	 * available at injection points in the component.
	 *
	 * <p>
	 * A special string (<code>"$"</code>) can be used to specify the name of the
	 * component as a configuration PID. The {@link CDIConstants#CDI_COMPONENT_NAME
	 * CDI_COMPONENT_NAME} constant holds this special string.
	 *
	 * <p>
	 * For example:
	 *
	 * <pre>
	 * {@code @FactoryPID(CDI_COMPONENT_NAME)}
	 * </pre>
	 */
	String value() default CDIConstants.CDI_COMPONENT_NAME;

}

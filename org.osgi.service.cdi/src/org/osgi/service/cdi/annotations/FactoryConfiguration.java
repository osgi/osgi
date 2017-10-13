/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Annotation used in collaboration with {@link ComponentScoped} to specify a
 * factory configuration.
 *
 * @author $Id$
 */
@Documented
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FactoryConfiguration {

	/**
	 * Support inline instantiation of the {@link FactoryConfiguration} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<FactoryConfiguration> implements FactoryConfiguration {

		private static final long serialVersionUID = 1L;

		/**
		 * @param pid the factory configuration pid
		 * @return an instance of {@link FactoryConfiguration}
		 */
		public static final Literal of(String pid) {
			return new Literal(pid);
		}

		private Literal(String pid) {
			_pid = pid;
		}

		@Override
		public String pid() {
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
	 * component as a configuration PID. The {@link Component#NAME} constant holds
	 * this special string. For example:
	 *
	 * <pre>
	 * &#64;FactoryConfiguration(pid = Component.NAME)
	 * </pre>
	 */
	String pid() default Component.NAME;

}

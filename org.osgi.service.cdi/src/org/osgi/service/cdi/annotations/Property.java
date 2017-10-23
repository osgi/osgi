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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used to associate properties with the CDI bean.
 * <p>
 * This is relevant in two scenarios:
 * <ul>
 * <li>When the bean injects configuration.</li>
 * <li>When the bean is published as a service.</li>
 * </ul>
 *
 * @author $Id$
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
@Repeatable(Properties.class)
public @interface Property {

	/**
	 * Support inline instantiation of the {@link Property} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Property> implements Property {

		private static final long serialVersionUID = 1L;

		/**
		 * @param properties
		 * @return an instance of {@link Property}
		 */
		public static Literal of(String properties) {
			return new Literal(properties);
		}

		private Literal(String property) {
			_property = property;
		}

		@Override
		public String value() {
			return _property;
		}

		private final String _property;

	}

	/**
	 * Properties for this bean.
	 * <p>
	 * Each property string is specified as {@code "name=value"}. The type of the
	 * property value can be specified in the name as {@code name:type=value}. The
	 * type must be one of the property types supported by the {@code type}
	 * attribute of the {@code property} element of a Component description.
	 * <p>
	 * To specify a property with multiple values, use multiple name, value pairs.
	 * For example, {@code @Property("foo=bar") @Property("foo=baz")}.
	 */
	String value();

}

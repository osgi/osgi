/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
import javax.enterprise.util.Nonbinding;
import javax.inject.Inject;
import javax.inject.Qualifier;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used with {@link Inject} in order to have Configuration Admin
 * configurations injected into CDI beans.
 * <p>
 * The values are PIDs of required configurations. The special value {@code $}
 * is used to refer to the host bean class name.
 */
@Qualifier
@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
public @interface Configuration {

	/**
	 * @return the PIDs of the required configurations
	 */
	@Nonbinding
	String[] value() default "$";

	/**
	 * @return if configurations for the specified pids are required or not,
	 *         default is {@code true}.
	 */
	boolean required() default true;

}

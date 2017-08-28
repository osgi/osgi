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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Scope;
import javax.inject.Singleton;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used to indicate a CDI bean marked with {@link Component
 * Component} has an OSGi {@code service.scope = bundle}.
 *
 * <p>
 * By default CDI beans annotated with {@link Component Component} only support:
 * <ul>
 * <li>{@link ApplicationScoped ApplicationScoped} or {@link Singleton
 * Singleton} - which translates to {@code service.scope = singleton}
 * <li>{@link BundleScoped BundleScoped} - which translates to
 * {@code service.scope = bundle}
 * <li>{@link Dependent Dependent} - which translates to
 * {@code service.scope = prototype}
 * </ul>
 * Any other scope will result in a definition error.
 * <p>
 * By default CDI beans without a scope have {@link Dependent Dependent} scope
 * which means developers should take care that their services have
 * {@code prototype} scope by default.
 */
@Documented
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
@Retention(value = RetentionPolicy.RUNTIME)
@Scope
@Target(value = {ElementType.TYPE})
public @interface BundleScoped {
	// marker annotation
}

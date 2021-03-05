/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.jpa.annotations;

import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.service.jpa.EntityManagerFactoryBuilder.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Requirement;

/**
 * This annotation can be used to require the JPA extender. It can be used
 * directly, or as a meta-annotation.
 *
 * @author $Id$
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
@Requirement(namespace = EXTENDER_NAMESPACE, //
		name = JPA_CAPABILITY_NAME, //
		version = JPA_SPECIFICATION_VERSION)
public @interface RequireJPAExtender {
	// A marker annotation indicating a JPA service extender requirement
}

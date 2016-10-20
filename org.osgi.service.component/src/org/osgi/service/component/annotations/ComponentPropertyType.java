/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identify the annotated annotation as a Component Property Type.
 * <p>
 * Component Property Types can be applied as annotations to the implementation
 * class of the Component. They can also be used as parameter types for the
 * component's life cycle methods {@link Activate}, {@link Deactivate}, and
 * {@link Modified}.
 * <p>
 * Component Property Types do not have to be annotated with this annotation to
 * be used as parameter types for life cycle methods but they must be annotated
 * with this annotation to be used as annotations on the implementation class of
 * the Component.
 * <p>
 * This annotation is not processed at runtime by Service Component Runtime. It
 * must be processed by tools and used to add a Component Description to the
 * bundle.
 * 
 * @see "Component Property Types."
 * @author $Id$
 * @since 1.4
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ComponentPropertyType {
	// meta-annotation
}

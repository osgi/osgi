/*
 * Copyright (c) OSGi Alliance (2011, 2017). All Rights Reserved.
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
 * Identify the annotated member as part of the activation of a Service
 * Component.
 * <p>
 * When the annotation is applied to a method or constructor, the method or
 * constructor is the {@code activate} method of the Component.
 * <p>
 * When the annotation is applied to a field, the field will contain an
 * activation argument of the Component. The field must be set after the
 * component instance constructor completes and before any other method, such as
 * the activate method, is called. That is, there is a <i>happens-before</i>
 * relationship between the field being set and any method being called on the
 * fully constructed component instance.
 * <p>
 * This annotation is not processed at runtime by Service Component Runtime. It
 * must be processed by tools and used to add a Component Description to the
 * bundle.
 * 
 * @see "The activate and activation-fields attributes of the component element of a Component Description."
 * @author $Id$
 * @since 1.1
 */
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD
})
public @interface Activate {
	// marker annotation
}

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

/**
 * Annotation applied to {@link javax.inject.Qualifier} for use on
 * {@link Reference} for conversion to service filters.
 *
 * The qualifier is converted to Map<String, String> following the rules defined
 * by the converter specification along with name mangling. The key/value pairs
 * are then 'ANDed' together.
 *
 * Multiple such Qualifiers' on a single Reference are 'ANDed' together and as
 * such can be combined.
 */
@Target(value = ElementType.ANNOTATION_TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ReferenceFilter {
	// marker annotation
}

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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.service.cdi.CdiConstants.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Provider;
import javax.inject.Qualifier;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.service.cdi.reference.ReferenceEvent;

/**
 * Annotation used on injection points informing the CDI container that the
 * injection should apply a service obtained from the OSGi registry.
 * <p>
 * 
 * <h2>Reference Names</h2>
 * 
 * The {@link javax.inject.Named} annotation may be used to specify a name to
 * serve as the base of the component properties used to configure the
 * reference. If not specified the name of the reference will be derived from
 * the fully qualifier class name and the reference injection point.
 * <p>
 * It is a definition error to have two references with the same name.
 * 
 * <h2>Reference Properties</h2>
 * 
 * Each reference is associated with the following component properties
 * <ul>
 * <li>{@code target := reference-name ".target"}
 * <p>
 * Value must be convertable to an LDAP filter</li>
 * <li>{@code cardinality := reference-name ".cardinality}
 * <p>
 * Value must be convertable to a positive integer</li>
 * </ul>
 * <p>
 * 
 * <h2>Service Representations</h2>
 * 
 * All sites where {@link Reference} can be applied accept one of several
 * representations of the consumed OSGi service.
 * <p>
 * Given that type {@code S} is a type under which the service is published then
 * type {@code T} represents one of the supported representations:
 * <ul>
 * <li>{@code T=S}</li>
 * <li>{@code T=ServiceReference<S>}</li>
 * <li>{@code T=Map<String, ?>}</li>
 * <li>{@code T=Map.Entry<Map<String, ?>, S>}</li>
 * <li>{@code T=ReferenceServiceObjects<S>}</li>
 * </ul>
 * 
 * <h2>Static References</h2>
 * 
 * Regular injection points. The injected containers are immutable.
 * 
 * <ul>
 * <li><strong>1..1</strong>
 * <ul>
 * <li>Reluctant: {@code @Inject @Reference T t;}</li>
 * <li>Greedy: {@code @Inject @Reference @Greedy T t;}</li>
 * </ul>
 * </li>
 * <li><strong>0..1</strong>
 * <ul>
 * <li>Reluctant: {@code @Inject @Reference Optional<T> ot;}</li>
 * <li>Greedy: {@code @Inject @Reference @Greedy Optional<T> ot;}</li>
 * </ul>
 * </li>
 * <li><strong>0..All</strong>
 * <ul>
 * <li>Reluctant: {@code @Inject @Reference List<T> lt;}</li>
 * <li>Greedy: {@code @Inject @Reference @Greedy List<T> lt;}</li>
 * <li>For {@code M..All}: Set the cardinality reference property to
 * {@code M}.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * <h2>Dynamic pull references</h2>
 * 
 * Injections of {@link Provider} of the respective static type. The result of
 * {@link Provider#get} is an immutable snapshot and is never {@code null}.
 * 
 * <ul>
 * <li><strong>1..1</strong>
 * <ul>
 * <li>Reluctant: {@code @Inject @Reference Provider<T> t;}</li>
 * <li>Greedy: {@code @Inject @Reference @Greedy Provider<T> t;}</li>
 * </ul>
 * </li>
 * <li><strong>0..1</strong>
 * <ul>
 * <li>Reluctant: {@code @Inject @Reference Provider<Optional<T>> ot;}</li>
 * <li>Greedy: {@code @Inject @Reference @Greedy Provider<Optional<T>> ot;}</li>
 * </ul>
 * </li>
 * <li><strong>0..All</strong>
 * <ul>
 * <li>Reluctant: Not defined</li>
 * <li>Greedy: {@code @Inject @Reference @Greedy Provider<List<T>> lt;}</li>
 * <li>For {@code M..All}: Set the cardinality reference property to
 * {@code M}.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * <h2>Dynamic push references</h2>
 * 
 * CDI events with payload {@link ReferenceEvent ReferenceEvent&lt;S&gt;}. The
 * events are not related to any injected dynamic pull references. I.e. are not
 * a prompt to pull a dynamic reference. Rather the users must store the service
 * objects in their own collections.
 * <p>
 * Notice that {@link Reference} is optional for event observers since the use
 * of the {@link ReferenceEvent ReferenceEvent&lt;S&gt;} payload is sufficient
 * to define a dynamic push reference.
 * <ul>
 * <li><strong>1..1</strong>
 * <ul>
 * <li>Reluctant: Not defined</li>
 * <li>Greedy: Not defined</li>
 * </ul>
 * </li>
 * <li><strong>0..1</strong>
 * <ul>
 * <li>Reluctant: Not defined</li>
 * <li>Greedy: Not defined</li>
 * </ul>
 * </li>
 * <li><strong>0..All</strong>
 * <ul>
 * <li>Reluctant: Not defined</li>
 * <li>Greedy: <pre>
 * {@code
 * public void serviceEvent(@Reference @Observes ReferenceEvent<S> event) {
 * ...
 * }}
 * </pre></li>
 * <li>For {@code M..All}: Set the cardinality reference property to
 * {@code M}.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author $Id$
 */
@Documented
@Qualifier
@Requirement(namespace = EXTENDER_NAMESPACE, name = CDI_CAPABILITY_NAME, version = CDI_SPECIFICATION_VERSION)
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface Reference {

	/**
	 * Support inline instantiation of the {@link Reference} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Reference> implements Reference {

		private static final long serialVersionUID = 1L;

		/**
		 * @param service
		 * @param target
		 * @return instance of {@link Reference}
		 */
		public static final Literal of(
				Class<?> service,
				String target) {

			return new Literal(service, target);
		}

		private Literal(
				Class<?> service,
				String target) {
			_service = service;
			_target = target;
		}

		@Override
		public Class<?> value() {
			return _service;
		}

		@Override
		public String target() {
			return _target;
		}

		private final Class<?>	_service;
		private final String	_target;

	}

	/**
	 * Specify the type of the service for this reference.
	 * <p>
	 * If not specified, the type of the service for this reference is derived from
	 * the injection point type.
	 * <p>
	 * If a value is specified it must be type compatible with (assignable to) the
	 * service type derived from the injection point type, otherwise a definition
	 * error will result.
	 */
	Class<?> value() default Object.class;

	/**
	 * The target property for this reference.
	 *
	 * <p>
	 * If not specified, no target property is set.
	 */
	String target() default "";

}

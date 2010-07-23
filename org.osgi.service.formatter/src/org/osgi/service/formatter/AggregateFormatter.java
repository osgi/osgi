/*
 * Copyright (c) OSGi Alliance (2008, 2010). All Rights Reserved.
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
package org.osgi.service.formatter;

/**
 * The Aggregate Formatter service aggregates the Formatter services present in
 * the service registry. That is, a Formatter service provides the formatting
 * facility for one or more types, an Aggregate Formatter service provides
 * formatting for any object by using the Formatter services present in the
 * service registry. When called, an Aggregate Formatter must use the
 * {@link Formatter#OSGI_FORMATTER_TYPE} property of the Formatter services to
 * determine which service to use. Finding the right formatter must follow the
 * following rules:
 * <ol>
 * <li>Exact class</li>
 * <li>Exact implemented interfaces in order of declaration</li>
 * <li>Recursively for super class/super interface</li>
 * <li>for each interface in order of declaration: recursively for each
 * interface</li>
 * </ol>
 * If for any of the previous step multiple services apply, the service ranking
 * rules must be used to determine the correct service.
 * 
 * An Aggregate Formatter must not register with a
 * {@link Formatter#OSGI_FORMATTER_TYPE} service property.
 * 
 * The Aggregate Formatter service uses the same signature as the Formatter Service,
 * the interface is therefore a marker interface to simplify getting access to the
 * aggregator.
 * 
 * @ThreadSafe
 * @ProviderInterface
 */
public interface AggregateFormatter extends Formatter {

}

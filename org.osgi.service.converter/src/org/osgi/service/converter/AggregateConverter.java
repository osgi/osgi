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
package org.osgi.service.converter;

/**
 * The Aggregate Converter service aggregates the Converter services present in
 * the service registry. That is, a Converter service provides the converting
 * facility for one or more types, an Aggregate Converter service provides
 * converting for any object by using the Converter services present in the
 * service registry, as well as providing a number of basic type conversions.
 * When called, an Aggregate Converter must use the
 * {@link Converter#OSGI_CONVERTER_TYPE} property of the Converter services to
 * determine which service to use. Finding the right converter must follow the
 * following rules:
 * 
 * The goal of the type conversion is to convert a source object {@code s} with
 * type {@code S} to a target type {@code T<P1..Pn>}. The Aggregator Converter
 * must attempt to find the first Converter service (in service.ranking order)
 * that can perform this conversion (as defined by the
 * {@link #canConvert(Object, ReifiedType)} method. This service must then be
 * used to do the conversion.
 * 
 * If no capable Converter service can be found, the Aggregator Converter
 * service must provide a number of basic conversions as described in the
 * specification. These conversions handle arrays, collections, and many
 * built-in types.
 * 
 * An Aggregate Formatter must not register with a
 * {@link Converter#OSGI_CONVERTER_TYPE} service property.
 * 
 * The Aggregate Converter service uses the same signature as the Converter
 * Service, this interface is therefore a marker interface to simplify getting
 * access to the Aggregator service.
 * 
 * @ThreadSafe
 * @ProviderInterface
 * @version $Id$
 */
public interface AggregateConverter extends Converter {
	// Marker sub interface.
}

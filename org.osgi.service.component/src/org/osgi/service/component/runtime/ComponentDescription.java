/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.component.runtime;

import java.util.Map;
import org.osgi.dto.DTO;
import org.osgi.dto.framework.BundleDTO;

/**
 * The {@code Component} interface represents the declaration of a component in
 * a Declarative Services descriptor.
 * 
 * @since 1.3
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentDescription extends DTO {
	/**
	 * The name of the component defined in the {@code component.name} attribute
	 * which may be {@code null}.
	 */
	public String				name;

	/**
	 * The {@linkplain BundleDTO bundle} declaring this component.
	 */
	public BundleDTO			bundle;

	/**
	 * The component factory name from {@code component.factory} attribute or
	 * {@code null} if this component is not defined as a component factory.
	 */
	public String				factory;

	/**
	 * The service scope for the service of this Component as defined by the
	 * {@code service/scope} attribute.
	 */
	public String				scope;

	/**
	 * The fully qualified name of the class implementing this component from
	 * the {@code component/implementation.class} attribute.
	 */
	public String				implementationClass;

	/**
	 * Whether the component is declared to be enabled by default ({@code true})
	 * as defined by the {@code component.enabled} attribute.
	 * 
	 */
	public boolean				defaultEnabled;

	/**
	 * Whether the component is an immediate or a delayed component as defined
	 * by the {@code component.immediate} attribute.
	 */
	public boolean				immediate;

	/**
	 * An array of service names provided by this component or {@code null} if
	 * the component is not registered as a service as defined by the
	 * {@code component/service/provide.interface} attributes.
	 */
	public String[]				serviceInterfaces;

	/**
	 * The declared properties of the component as defined by the
	 * {@code component/property} and {@code component/properties} elements.
	 */
	public Map<String, Object>	properties;

	/**
	 * An array of {@link Reference} instances representing the service
	 * references (or dependencies) of this component as defined in the
	 * {@code component/reference} elements.
	 */
	public Reference[]			references;

	/**
	 * The name of the method to be called when the component is being activated
	 * as defined in the {@code component.activate} attribute or {@code null} if
	 * not explicitly declared.
	 */
	public String				activate;

	/**
	 * The name of the method to be called when the component is being
	 * deactivated as defined in the {@code component.deactivate} attribute or
	 * {@code null} if not explicitly declared.
	 */
	public String				deactivate;

	/**
	 * The name of the method to be called when the component's configuration is
	 * being updated as defined in the {@code component.modified} attribute or
	 * {@code null} if not declared.
	 */
	public String				modified;

	/**
	 * The configuration policy declared in the
	 * {@code component.configuration-policy} attribute. If the component
	 * descriptor is a Declarative Services 1.0 descriptor or not configuration
	 * policy has been declared, the default value <i>optional</i> is returned.
	 * <p>
	 * The returned string is one of the three policies defined in the
	 * Declarative Services specification 1.1:
	 * <dl>
	 * <dt>optional</dt>
	 * <dd>Configuration from the Configuration Admin service is supplied to the
	 * component if available. Otherwise the component is activated without
	 * Configuration Admin configuration. This is the default value reflecting
	 * the behaviour of Declarative Services 1.0</dd>
	 * <dt>require</dt>
	 * <dd>Configuration is required. The component remains unsatisfied until
	 * configuartion is available from the Configuration Admin service.</dd>
	 * <dt>ignore</dt>
	 * <dd>Configuration is ignored. No Configuration Admin service
	 * configuration is supplied to the component.</dd>
	 * </dl>
	 */
	public String				configurationPolicy;

	/**
	 * The unique ID of this component managed by the Service Component Runtime.
	 * This value is also available as the {@code component.id} service
	 * registration property of component configurations registered as services.
	 */
	public long					id;
}

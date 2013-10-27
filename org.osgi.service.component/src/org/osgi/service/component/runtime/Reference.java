/*
 * Copyright (c) OSGi Alliance (2004, 2013). All Rights Reserved.
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

import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * The {@code Reference} interface represents a single reference (or dependency)
 * to a service used by a Component as declared in the {@code reference}
 * elements of a Declarative Services descriptor.
 *
 * @since 1.3
 * @NotThreadSafe
 * @author $Id$
 */
public class Reference extends DTO {
    /**
     * The name of this Reference as defined by the {@code reference.name}
     * attribute or {@code null} if not declared.
     */
    public String name;

    /**
     * The name of the service used by this reference as defined by the
     * {@code reference.interface} attribute.
     */
    public String interfaceName;

    /**
     * Whether this reference is optional as defined by the lower bound of the
     * {@code reference.cardinality}. In other words this field is set to
     * {@code true} if the cardinality is <em>0..1</em> or <em>0..n</em>.
     */
    public boolean optional;

    /**
     * Whether this reference is multiple as defined by the upper bound of the
     * {@code reference.cardinality}. In other words this field is set to
     * {@code true} if the cardinality is <em>0..n</em> or <em>1..n</em>.
     */
    public boolean multiple;

    /**
     * Whether the reference is statically or dynamically bound as defined by
     * the {@code reference.policy} attribute.
     */
	public String			policy;

    /**
     * Policy of handling of availability of a better service as defined by the
     * {@code reference.policy-option} attribute.
     */
	public String			policyOption;

    /**
     * The value of the target property of this reference as defined by the
     * {@code reference.target} attribute or {@code null} if not declared.
     */
    public String target;

    /**
     * The name of the method called if a service is being bound to the
     * component as defined by the {@code reference.bind} attribute or
     * {@code null} if no such method is declared.
     */
    public String bind;

    /**
     * The name of the method called if a service is being unbound from the
     * component as defined by the {@code reference.unbind} attribute or
     * {@code null} if no such method is declared.
     */
    public String unbind;

    /**
     * The name of the method called if the bound service service is updated as
     * defined by the {@code reference.updated} attribute or {@code null} if no
     * such method is declared.
     */
    public String updated;

	/**
	 * The requested service scope for this Reference as defined by the
	 * {@code reference.scope} attribute.
	 */
	public ReferenceScope			scope;
}

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

import org.osgi.dto.DTO;
import org.osgi.dto.framework.ServiceReferenceDTO;

/**
 * The {@code BoundReference} interface represents the actual service binding of
 * a service reference declared in the {@linkplain Reference reference element}
 * of the component declaration.
 * 
 * @since 1.3
 * @NotThreadSafe
 * @author $Id$
 */
public class BoundReference extends DTO {
    /**
     * Returns the {@code component/reference} element of the component
     * descriptor defining this bound reference.
     */
	public Reference				reference;

    /**
	 * Returns whether this reference is satisfied. An
	 * {@linkplain Reference#optional optional} reference is always satisfied.
	 * Otherwise {@code true} is only returned if at least one service is bound.
	 */
	public boolean					satisfied;

    /**
	 * The value of the actual target value used to select services to bind to.
	 * Initially (without overwriting configuration) this method provides access
	 * to the {@code component/reference.target} attribute of the reference
	 * declaration. If configuration overwrites the target property, this method
	 * returns the value of the component property whose name is derived from
	 * the {@linkplain Reference#name reference name} plus the suffix
	 * <em>.target</em>. If no target property exists this field is set to
	 * {@code null}.
	 */
	public String					target;

    /**
     * An array of {@code ServiceReferenceDTO} instances representing the bound
     * services. If no services are actually bound, this field is set to
     * {@code null}.
     */
	public ServiceReferenceDTO[]	serviceReferences;
}

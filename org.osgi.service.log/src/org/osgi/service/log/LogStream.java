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

package org.osgi.service.log;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceObjects;
import org.osgi.util.pushstream.PushStream;

/**
 * LogStream service for receiving log information.
 * <p>
 * This service must be registered as a {@link PrototypeServiceFactory
 * prototype} service so that each user can receive a new, distinct
 * {@link PushStream} of {@link LogEntry} objects. Users of this service must
 * obtain this service using {@link ServiceObjects} to get a new, distinct
 * {@link PushStream}. For example, to inject a {@code LogStream} service into a
 * Declarative Services component:
 * <pre>
 * &#64;Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
 * private LogStream logs;
 * </pre>
 * <p>
 * When a {@code LogStream} service object is released and
 * {@link PrototypeServiceFactory#ungetService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration, Object)}
 * is called, {@link #close()} must be called on the {@code LogStream} to ensure
 * the stream is closed.
 * 
 * @ThreadSafe
 * @since 1.4
 * @author $Id$
 */
@ProviderType
public interface LogStream extends PushStream<LogEntry> {
	// no additional methods
}

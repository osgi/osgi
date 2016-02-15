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
package org.osgi.service.transaction.control.jdbc;

import java.sql.Connection;

import org.osgi.service.transaction.control.ResourceProvider;

/**
 * A specialised {@link ResourceProvider} suitable for obtaining JDBC
 * connections.
 * <p>
 * Instances of this interface may be available in the Service Registry, or can
 * be created using a {@link JDBCConnectionProviderFactory}.
 */
public interface JDBCConnectionProvider extends ResourceProvider<Connection> {
	/**
	 * This interface specialises the ResourceProvider for creating JDBC
	 * connections
	 */
}

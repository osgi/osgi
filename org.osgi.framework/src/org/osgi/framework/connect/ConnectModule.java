/*
 * Copyright (c) OSGi Alliance (2019). All Rights Reserved.
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
package org.osgi.framework.connect;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;

/**
 * A connect module instance is used by a {@link Framework framework} to load
 * content and obtain a class loader for a bundle installed in the framework.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public interface ConnectModule {
	/**
	 * Opens the content of this connect module. The framework will open the
	 * content when it needs to access the content for a bundle associated with
	 * the connect module. The framework may lazily open the content until the
	 * first request is made to access the bundle content.
	 * 
	 * @return the content of this connect module
	 * @throws IOException if an error occurred opening the content
	 */
	ConnectContent open() throws IOException;

	/**
	 * Returns a class loader for this connect module. The
	 * {@link Optional#empty() empty} value is returned if the framework should
	 * handle creating a class loader for the bundle associated with this
	 * connect module.
	 * <p>
	 * This method is called by the framework for {@link Bundle#RESOLVED
	 * resolved} bundles only and will be called at most once while a bundle is
	 * resolved. If a bundle associated with a connect module is refreshed and
	 * resolved again the framework will ask the module for the class loader
	 * again. This allows for a connect module to reuse or create a new class
	 * loader each time the bundle is resolved.
	 * 
	 * @return a class loader for the module.
	 */
	Optional<ClassLoader> getClassLoader();

	/**
	 * @return null if framework should handle
	 */
	// TODO remove this method?
	Optional<Map<String,String>> getHeaders();
}
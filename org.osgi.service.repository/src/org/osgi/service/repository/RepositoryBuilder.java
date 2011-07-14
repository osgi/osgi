/*
 * Copyright (c) OSGi Alliance (2006, 2011). All Rights Reserved.
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

// This document is an experimental draft to enable interoperability
// between bundle repositories. There is currently no commitment to 
// turn this draft into an official specification.  
package org.osgi.service.repository;

import java.io.IOException;
import java.net.URL;

/**
 * A repository builder service interface to allow third party code to simply build
 * repositories by pointing at a URL location.
 * 
 * <p>
 * A trivial implementation of this interface can attempt to build a repository based on
 * a file system directory:
 * 
 * <pre>public Repository build(URL location) throws IOException {
 *  Repository built = null;
 *  
 *  if ("file".equals(location.getProtocol())) {
 *    try {
 *      File file = new File(location.toURI());
 *      
 *      if (file.isDirectory()) {
 *        List&lt;Resource&gt; resources = scanResources(file);          
 *        built = new RepositoryImpl(resources);
 *      }
 *    } catch (URISyntaxException e) {
 *      // can't be a file repo
 *    }      
 *  }
 *  
 *  return built;
 *}</pre>
 * 
 * 
 * @ThreadSafe
 */
public interface RepositoryBuilder {
  /**
   * Attempts to build a Repository from the supplied location.
   * 
   * @param location The location of the repository
   *  
   * @return A repository or null if this builder does not know how to build a repository
   * from the supplied location
   * 
   * @throws IOException if building this repository fails due to an unexpected problem
   */
  Repository build(URL location) throws IOException;
}

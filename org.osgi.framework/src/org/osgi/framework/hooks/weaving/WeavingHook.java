/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.framework.hooks.weaving;

import org.osgi.framework.wiring.BundleWiring;

/**
 * This interface should be used to register weaving implementations within
 * an OSGi framework
 * 
 * @ThreadSafe
 * @version $Id$
 * 
 */
public interface WeavingHook {

  /**
   * <p>
   *   Implementations of this service are called when a class is being loaded
   *   by the framework, and have an opportunity to re-write the class 
   *   bytecode that represents the class being loaded. WeavingHooks may also
   *   ask the framework to wire in additional dynamic imports to the bundle by
   *   modifying the {@link WovenClass#getDynamicImports()} collection.
   * </p>
   *
   * <p>
   *   If multiple {@link WeavingHook} implementations are present in the
   *   service registry then they are called in the order in which the service
   *   registry returns them. The first {@link WeavingHook} is called using the 
   *   a {@link WovenClass} that contains the raw class bytes. Subsequent
   *   services are then called with the same {@link WovenClass} given to
   *   the previous service. To best coexist when there are multiple services it
   *   is recommended that {@link WeavingHook} implementations set their service
   *   ranking according to how invasive the changes they wish to make to
   *   classes are. More invasive transformations should be applied first, and
   *   so should be given a higher ranking. Running from most invasive
   *   to least invasive:
   *   </p>
   *   <ol>
   *     <li>WeavingHooks that make changes to the Class inheritance hierarchy
   *     </li>
   *     <li>WeavingHooks that make changes to method signatures of a class,
   *        including additions and deletions</li>
   *     <li>WeavingHooks that make changes to fields in a class,
   *         including additions and deletions</li>
   *     <li>WeavingHooks that make changes to method bodies or constant pool
   *         entries</li>
   *     <li>WeavingHooks that make changes to annotations</li>
   *   </ol>
   * <p>
   *   If this {@link WeavingHook} does not believe that the class bytes 
   *   represent a valid class file, or that the class cannot be woven for some
   *   other reason then no modifications should be made to the 
   *   {@link WovenClass} and a {@link ClassFormatError} should be thrown. The
   *   framework will log this error, and then continue to call the next
   *   {@link WeavingHook} service. If the {@link #weave(BundleWiring, 
   *   WovenClass)} method uses any other abnormal return then the framework
   *   will log the error and abandon loading the class.
   * </p>
   * 
   * @param wiring - The current wiring for the bundle
   * @param classData - The {@link WovenClass} instance that represents the
   *                    data that will be used to define the class.
   * 
   * @throws ClassFormatError If the {@link WeavingHook} detects a problem with
   *                          the class or bundle that means it cannot perform
   *                          its transformations but it does not wish the
   *                          framework to fail loading the class then it should
   *                          throw a {@link ClassFormatError}. If any other
   *                          {@link Throwable} is thrown then the framework
   *                          will fail to load the class. If a 
   *                          {@link ClassFormatError} is thrown then the
   *                          WeavingHook should not have made any modifications
   *                          to the {@link WovenClass}, as this will be passed
   *                          to the next {@link WeavingHook} in the chain.
   */
  public void weave(BundleWiring wiring, WovenClass classData) throws 
                                                               ClassFormatError;
  
}

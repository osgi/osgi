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

import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Collections;

import org.osgi.framework.wiring.BundleWiring;


/**
 * This class represents the current progress of a {@link WeavingHook} chain.
 * It allows access to the most recently transformed class bytes, and to any 
 * additional packages that should be added to the bundle as dynamic imports.
 * Instances of this interface will be passed by the framework to 
 * {@link WeavingHook#weave(BundleWiring, WovenClass)}, and should only be 
 * modified by a {@link WeavingHook} within the invocation of that method.
 *
 * @NotThreadSafe
 * @version $Id$
 */
public interface WovenClass {
  
  /**
   * Returns a copy of the bytes most recently provided to 
   * {@link #setBytes(byte[])} or the raw, untransformed class bytes if no 
   * {@link WeavingHook} has provided new bytes.
   * 
   * @return A copy of the bytes that will be used to define this class.
   */
  public byte[] getBytes();
  
  /**
   * Replace the bytes that this class will be defined with. This method should
   * not be called outside invocations of 
   * {@link WeavingHook#weave(BundleWiring, WovenClass)}, and once 
   * {@link #isWeavingComplete()} returns true calling this method will result
   * in an {@link IllegalStateException}.
   * 
   * @param newBytes The new transformed bytecode that should be used to define
   *                 class.
   * @throws NullPointerException If newBytes is null.
   * @throws IllegalStateException If {@link #isWeavingComplete()} is true
   */
  public void setBytes(byte[] newBytes) throws NullPointerException, 
                                               IllegalStateException;

  /**
   * Retrieve the collection of packages to add as dynamic imports. This 
   * Collection will not be a copy, and any additions or deletions made by a 
   * {@link WeavingHook} will be reflected in future calls to this method.
   * 
   * @return A {@link Collection} to which Import-Package syntax Strings can
   *         be added. This Collection will throw IllegalArgumentException if
   *         any Strings added to it are not well-formed according to the OSGi
   *         Import-Package syntax. This Collection should not be modified
   *         outside an invocation of {@link WeavingHook#weave(BundleWiring, 
   *         WovenClass)}. Once {@link #isWeavingComplete()} returns true the 
   *         Collection returned by this method will be unmodifiable, as per 
   *         {@link Collections#unmodifiableCollection(Collection)}.
   */
  public Collection<String> getDynamicImports();
  
  /**
   * This method can be used to determine whether a Weaving chain has finished
   * operating.
   * 
   * @return <code>true</code> if no more weavers are left to be called, 
   *         <code>false</code> otherwise.
   */
  public boolean isWeavingComplete();
  
  /**
   * Allows access to the name of the class being woven
   * 
   * @return The name of the class.
   */
  public String getClassName();

  /**
   * This method can be used to access the protection domain in which
   * this class will be defined.
   * @return The Protection domain that will be used to define this class,
   *         which may be null if no {@link ProtectionDomain} will be used.
   */
  public ProtectionDomain getProtectionDomain();
  
  /**
   * <p>
   * This method is provided for future use when weaving classes that have
   * already been defined. 
   * </p>
   * <p>
   * If the class referred to by this {@link WovenClass} has already been loaded
   * then this method will return the existing {@link Class} object that
   * represents the loaded class. Otherwise this method will return null.
   * </p>
   * 
   * @return The previous results from loading the class, or null if this is 
   *         the first time the class is being defined.
   */
  public Class<?> getPreviousClassDefinition();
}

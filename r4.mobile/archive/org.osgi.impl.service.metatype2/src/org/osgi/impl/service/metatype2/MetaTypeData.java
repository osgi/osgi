/*
* $Header$
* 
* Copyright (c) OSGi Alliance (2004). All Rights Reserved.
* 
* Implementation of certain elements of the OSGi Specification may be subject
* to third party intellectual property rights, including without limitation,
* patent rights (such a third party may or may not be a member of the OSGi
* Alliance). The OSGi Alliance is not responsible and shall not be held
* responsible in any manner for identifying or failing to identify any or all
* such third party intellectual property rights.
* 
* This document and the information contained herein are provided on an "AS IS"
* basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
* HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
* MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
* OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
* USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
* EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
* CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
* 
* All Company, brand and product names may be trademarks that are the sole
* property of their respective owners. All rights reserved.
*/
package org.osgi.impl.service.metatype2;

import org.osgi.service.metatype.MetaTypeProvider;


/**
 * 
 * @version $Revision$
 */
class MetaTypeData {

  private String version;
  private String category;
  private String id;
  
  private MetaTypeProvider provider;

  
  MetaTypeData(String id, String category, String version, MetaTypeProvider provider) {
    this.id = id;
    this.category = category;
    this.version = version;
    
    this.provider = provider;
  }
  
  
  /**
   * @return Returns the category.
   */
  String getCategory() {
    return category;
  }
  
  /**
   * @return Returns the id.
   */
  String getId() {
    return id;
  }
  
  /**
   * @return Returns the provider.
   */
  MetaTypeProvider getProvider() {
    return provider;
  }
  
  /**
   * @return Returns the version.
   */
  String getVersion() {
    return version;
  }
  
  /**
   * @param category The category to set.
   */
  void setCategory(String category) {
    this.category = category;
  }
  
  /**
   * @param id The id to set.
   */
  void setId(String id) {
    this.id = id;
  }
  
  /**
   * @param version The version to set.
   */
  void setVersion(String version) {
    this.version = version;
  }
  
  boolean isSame(String newCategory, String newId) {
    return id.equals(newId) && areEqual(category, newCategory);
  }
  
  private boolean areEqual(String source, String target) {
    return source != null ? source.equals(target) : target == null; 
  }

}

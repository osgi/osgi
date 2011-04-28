/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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
package org.osgi.service.obr;

public interface OBRConstants {
	  /**
	   * Namespace for OSGi bundle revisions
	   */
	  final String BUNDLE_NAMESPACE = "osgi.bundle";
	  
	  /**
	   * Checksum attribute of a revision
	   */
	  final String CHECKSUM_ATTRIBUTE = "checksum";

	  /**
	   * The checksum algorithm used to calculate the {@link #CHECKSUM_ATTRIBUTE}
	   * if not specified this is assumed to be SHA-256 - TODO need default?
	   */
	  final String CHECKSUM_ALGO_ATTRIBUTE = "checksumAlgo";

	  /**
	   * TODO
	   */
	  final String COPYRIGHT_ATTRIBUTE = "copyright";

	  /**
	   * A human readable description of this revision 
	   */
	  final String DESCRIPTION_ATTRIBUTE = "description";

	  /**
	   * A URL where documentation for this revision can be accessed 
	   */
	  final String DOCUMENTATION_URL_ATTRIBUTE = "documentation";

	  /**
	   * TODO
	   */
	  final String LICENSE_URL_ATTRIBUTE = "license";

	  /**
	   * If present this must match the value returned from {@link #getLocation()}
	   * TODO mandatory?
	   */
	  final String LOCATION_ATTRIBUTE = "location";

	  /**
	   * Identifies the type of revision that this represents.
	   * TODO mandatory? 
	   */
	  final String NAMESPACE_ATTRIBUTE = "namespace";

	  /**
	   * If present this must match the value returned from {@link #getSymbolicName()}
	   * TODO mandatory
	   */
	  final String SYMBOLIC_NAME_ATTRIBUTE = "symbolicName";

	  /**
	   * TODO
	   */
	  final String SCM_URL_ATTRIBUTE = "scm";

	  /**
	   * The size of this revision in bytes. If this revision is a meta revision
	   * the corresponding attribute value must equal -1
	   */
	  final String SIZE_ATTRIBUTE = "size";

	  /**
	   * If present this must match the value returned from {@link #getVersion() getVersion().toString()}
	   * TODO mandatory?
	   */
	  final String VERSION_ATTRIBUTE = "version";

	  /**
	   * All attributes defined in this interface
	   */
	  final String[] ATTRIBUTES = { CHECKSUM_ATTRIBUTE,
	      CHECKSUM_ALGO_ATTRIBUTE, COPYRIGHT_ATTRIBUTE, DESCRIPTION_ATTRIBUTE,
	      DOCUMENTATION_URL_ATTRIBUTE, LICENSE_URL_ATTRIBUTE, LOCATION_ATTRIBUTE,
	      SYMBOLIC_NAME_ATTRIBUTE, NAMESPACE_ATTRIBUTE, SCM_URL_ATTRIBUTE, SIZE_ATTRIBUTE,
	      VERSION_ATTRIBUTE };
}

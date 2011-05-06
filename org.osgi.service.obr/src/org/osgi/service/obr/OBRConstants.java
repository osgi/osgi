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

import org.osgi.model.resource.Resource;
import org.osgi.model.resource.ResourceConstants;

public interface OBRConstants {
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
	   * TODO
	   */
	  final String SCM_URL_ATTRIBUTE = "scm";

	  /**
	   * The size of this revision in bytes. If this revision is a meta revision
	   * the corresponding attribute value must equal -1
	   */
	  final String SIZE_ATTRIBUTE = "size";

	  /**
	   * All attributes defined in this interface
	   */
	  final String[] ATTRIBUTES = { CHECKSUM_ATTRIBUTE,
	      CHECKSUM_ALGO_ATTRIBUTE, COPYRIGHT_ATTRIBUTE, DESCRIPTION_ATTRIBUTE,
	      DOCUMENTATION_URL_ATTRIBUTE, LICENSE_URL_ATTRIBUTE, ResourceConstants.RESOURCE_CONTENT_ATTRIBUTE,
	      ResourceConstants.RESOURCE_SYMBOLIC_NAME_ATTRIBUTE, ResourceConstants.RESOURCE_NAMESPACE_ATTRIBUTE, SCM_URL_ATTRIBUTE, SIZE_ATTRIBUTE,
	      ResourceConstants.RESOURCE_VERSION_ATTRIBUTE };
}

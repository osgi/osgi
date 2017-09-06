/*
 * Copyright (c) OSGi Alliance (2010, 2017). All Rights Reserved.
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
package org.osgi.service.clusterinfo;

import org.osgi.dto.DTO;

/**
 * Data Transfer Object for a NodeStatus Service.
 */
public class NodeStatusDTO extends DTO {
	
	/**
	 * Prefix used for the converter
	 */
	public static final String	PREFIX_	= "osgi.clusterinfo.";

	/**
	 * The globally unique ID for this node. For example the Docker ID if this
	 * node is a Docker container, or the framework UUID if this node is an OSGi
	 * framework.
	 */
	public String id;

	/**
	 * The name of the cluster this node belongs to.
	 */
	public String	cluster;
	
	/**
	 * An optional parentID indicating this node is part of or embedded in
	 * another node. For example multiple virtual machines could run on the same
	 * physical node.
	 */
	public String	parentid;

	/**
 	 * The endpoint(s) at which this node can be accessed from the
	 * viewpoint of the consumer of the service.
	 */
	public String[] endpoints;
	
	/**
	 * Private endpoint(s) at which this node can be accessed from within the
	 * cluster only.
	 */
	public String[]	privateEndpoints;

	/**
	 * The vendor name of the cloud/environment in which the node operates.
	 */
	public String vendor;
	
	/**
	 * The version of the cloud/environment in which the node operates. The
	 * value follows the versioning scheme of the cloud provider and may
	 * therefore not comply with the OSGi versioning syntax.
	 */
	public String version;
	
	/**
	 * ISO 3166-1 alpha-3 location where this node instance is
	 * running, if known.
	 */
	public String country;
	
	/**
	 * ISO 3166-2 location where this node instance is running,
	 * if known. This location is more detailed than the country code
	 * as it may contain province or territory.
	 */
	public String location;
	
	/**
	 * Something smaller than a country and bigger than a location
	 * (e.g. us-east-1 or other cloud-specific location)
	 */
	public String region;
	
	/**
	 * Regions are often subdivided in zones that represent different
	 * physical locations. The zone can be provided here.
	 */
	public String zone;
	
	/**
	 * Tags associated with this node that can be contributed to by
	 * the provider and also by bundles.
	 */
	public String[] tags;
	
}

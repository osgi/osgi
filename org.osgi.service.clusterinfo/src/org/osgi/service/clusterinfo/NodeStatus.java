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

import java.util.Map;

/**
 * The NodeStatus service represents a node in the cluster.
 * <p>
 * A node could represent an entity available in the network that is not
 * necessarily running an OSGi framework, such as a database or a load balancer.
 * </p>
 */
public interface NodeStatus {
	
	/**
	 * Get the current metrics or other dynamic data from the node. Nodes may
	 * support custom metrics and as such the caller can request those metrics
	 * by name. The caller can specify the metric names to avoid having to
	 * compute and send all metrics over, if the caller is only interested in a
	 * subset of the available metrics.
	 * 
	 * @param names a set of metric names that have to be obtained from the
	 *            node. Of no names are specified all available metrics will be
	 *            obtained. If a metric is requested that is not available by
	 *            the node this metric is ignored and not present in the
	 *            returned map.
	 * @return Map with the current node metrics
	 */
	Map<String, Object> getMetrics(String... names);

}

/*******************************************************************************
 * $Date$
 *  
 * Copyright (c) OSGi Alliance (2006, 2007). All Rights Reserved.
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
 *******************************************************************************/
package org.osgi.service.navigation;

import java.util.List;

import org.osgi.util.measurement.Measurement;

public interface Path extends List {

	/**
	 * ### Should we return the index?
	 * ### what happens when you have not planned this Route?
	 * ### will it return at the end?
	 * ### what happens when the nav system replans because of some
	 *     reason?
	 * 
	 * @return
	 */
	RoadSegment getNextRoadSegment(RoadSegment segment);
	RoadSegment getPreviousRoadSegment(RoadSegment segment);
	RoadSegment getCurrentRoadSegment();
	

	Measurement getTime2RoadSegment(RoadSegment from, RoadSegment to);
	Measurement getDistance2RoadSegment(RoadSegment from, RoadSegment to);
}

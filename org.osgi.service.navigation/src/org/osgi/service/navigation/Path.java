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

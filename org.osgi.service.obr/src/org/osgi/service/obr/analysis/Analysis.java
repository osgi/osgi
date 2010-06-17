package org.osgi.service.obr.analysis;

import org.osgi.service.obr.Part;
import org.osgi.service.obr.Requirement;

public interface Analysis {
	Requirement[] getUnsatisfiedRequirements();

	Part[] getRequiredParts();
	
	Part[] getOptionalParts();
	
	/**
	 * Parts may need to be installed in certain orders to 
	 * satisfy resolution constraints.
	 * 
	 * @return
	 */
	int getPartLoops();
	
	/**
	 * Get the parts in the that they need to be installed to
	 * satisfy resolution constraints
	 * 
	 * @return
	 */
	Part[] getPartLoop(int i);

	/**
	 * Find the requirements that brought in a given part.
	 * 
	 * @param part
	 * @return
	 */
	Requirement[] getConsumers(Part part);
	
	/**
	 * Find the parts that satisfy a given requirement.
	 * 
	 * @param requirement
	 * @return
	 */
	Part[] getProviders(Requirement requirement);
}

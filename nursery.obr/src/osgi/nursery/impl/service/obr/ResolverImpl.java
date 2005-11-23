/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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

package osgi.nursery.impl.service.obr;

import java.util.*;

import osgi.nursery.service.obr.*;


public class ResolverImpl implements Resolver {
	RepositoryAdminImpl	parent;
	List				resources = new ArrayList();
	Collection			missing;
	Collection				required;
	
	ResolverImpl( RepositoryAdminImpl parent ) {
		this.parent = parent;
	}
	public void add(Resource resource) {
		resources.add(resource);
	}

	public Requirement[] getMissingRequirements() {
		Requirement [] result = new Requirement[ missing.size()];
		missing.toArray(result);
		return result;
	}

	public Resource[] getRequestedResources() {
		return new Resource[0];
	}

	public Resource[] getIncludedResources() {
		Resource [] result = new Resource[ required.size()];
		required.toArray(result);
		return result;
	}
	public boolean resolve() {
		return parent.resolve(this);
	}

	public void deploy() {
	}

}

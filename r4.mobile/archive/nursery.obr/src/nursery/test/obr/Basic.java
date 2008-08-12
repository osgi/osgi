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

package nursery.test.obr;

import junit.framework.TestCase;

import nursery.obr.resource.*;

import org.osgi.framework.Version;



public class Basic extends TestCase {

	public void testRequirements() {
		RequirementImpl	rq = new RequirementImpl("package");
		rq.setFilter("(&(package=org.osgi.util.tracker)(version=1.3.0))");
		
		CapabilityImpl	cap = new CapabilityImpl("package");
		cap.addProperty("package", "org.osgi.util.tracker");
		cap.addProperty("version", new Version("1.3"));
		assertTrue("Check equal version", rq.isSatisfied(cap));
		
		rq.setFilter("(&(package=org.osgi.util.tracker)(version>=1.2.0))");
		assertTrue("1.3.0 > 1.2.0", rq.isSatisfied(cap));
		
		rq.setFilter("(&(package=org.osgi.util.tracker)(version>=1.3.0))");
		assertTrue("1.3.0 >= 1.3.0", rq.isSatisfied(cap));
		
		rq.setFilter("(&(package=org.osgi.util.tracker)(version>=1.4.0))");
		assertFalse("1.3.0 >= 1.4.0", rq.isSatisfied(cap));
		
		rq.setFilter("(&(package=org.osgi.util.tracker)(version<=1.4.0))");
		assertTrue("1.3.0 >= 1.4.0", rq.isSatisfied(cap));
	}
}

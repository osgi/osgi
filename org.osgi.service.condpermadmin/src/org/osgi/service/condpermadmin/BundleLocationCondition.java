/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.condpermadmin;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;

import org.osgi.framework.Bundle;

public class BundleLocationCondition implements Condition {
	boolean match;

    public BundleLocationCondition(final Bundle b, String location) {
		if ((location == null) || (b == null)) {
			match = false;
			return;
		}
		String l = (String) AccessController
				.doPrivileged(new PrivilegedAction() {
					public Object run() {
						return b.getLocation();
					}
				});
		match = l.equals(location);
	}

	public boolean isEvaluated() {
		return true;
	}

	public boolean isMutable() {
		return false;
	}

	public boolean isSatisfied() {
		return match;
	}

	public boolean isSatisfied(Condition[] conds, Dictionary oneShotData) {
		for (int i = 0; i < conds.length; i++) {
			if (!((BundleLocationCondition) conds[i]).match)
				return false;
		}
		return true;
	}
}

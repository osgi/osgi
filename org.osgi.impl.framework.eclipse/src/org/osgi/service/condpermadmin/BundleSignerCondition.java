/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.condpermadmin;

import java.util.Dictionary;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.osgi.framework.Bundle;

/**
 * This condition checks the signer of a bundle. Since the bundle's signer can only change
 * when the bundle is updated, this condition is immutable.
 * <p>
 * The condition expressed using a single String that specifies a Distinguished Name (DN)
 * chain to match bundle signers against. DN's are encoded using IETF RFC 2253. Usually
 * signers use certificates that are issued by certificate authorities, which also have a
 * corresponding DN and certificate. The certificate authorities can form a chain of trust
 * where the last DN and certificate is known by the framework. The signer of a bundle is
 * expressed as signers DN followed by the DN of its issuer followed by the DN of the next
 * issuer until the DN of the root certificate authority. Each DN is separated by a semicolon.
 * <p>
 * A bundle can satisfy this condition if one of its signers has a DN chain that matches the
 * DN chain used to construct this condition.
 * Wildcards (`*') can be used to allow greater flexibility in specifying the DN chains.
 * Wildcards can be used in place of DNs, RDNs, or the value in an RDN. If a wildcard is
 * used for a value of an RDN, the value must be exactly "*" and will match any value for
 * the corresponding type in that RDN.  If a wildcard is used for a RDN, it must be the
 * first RDN and will match any number of RDNs (including zero RDNs).   
 * 
 * @version $Revision$
 */
public class BundleSignerCondition implements Condition {
	boolean satisfied;

	/**
	 * Constructs a BundleSignerCondition for the given bundle to check against the pattern specified in <code>dnChain</code>.
	 * 
	 * @param bundle the bundle to check this condition against.
	 * @param dnChain the chain of distinguished names pattern to check against the signer of the bundle.
	 */
	public BundleSignerCondition(Bundle bundle, String dnChain) {
		AbstractBundle ab = (AbstractBundle) bundle;
		satisfied = ab.getBundleData().matchDNChain(dnChain);
	}


	/**
	 * Always returns true, since this condition is immutable.
	 * @return always returns true;
	 * @see org.osgi.service.condpermadmin.Condition#isPostponed()
	 */
	public boolean isPostponed() {
		return false;
	}

	/**
	 * Returns true if the bundle used to construct this condition matches
	 * the condition's DNChain expression.
	 * @return true if the DNChain expression is matched.
	 * @see org.osgi.service.condpermadmin.Condition#isSatisfied()
	 */
	public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * Always returns false, since this condition is immutable.
	 * @return always returns false.
	 * @see org.osgi.service.condpermadmin.Condition#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * This method simply invokes the isSatisfied() method of all the conds
	 * and returns true only if all of the method invocations return true.
	 * @param conds the conditions to check for satisfiability.
	 * @param context not used.
	 * @return true only if all the conds are true.
	 * @see org.osgi.service.condpermadmin.Condition#isSatisfied(org.osgi.service.condpermadmin.Condition[], java.util.Dictionary)
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		for (int i = 0; i < conds.length; i++) {
			if (!conds[i].isSatisfied()) {
				return false;
			}
		}
		return true;
	}
}

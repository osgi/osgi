/*
 * Copyright (c) 2004, Nokia
 * COMPANY CONFIDENTAL - for internal use only
 */
package org.osgi.service.condpermadmin;

import java.util.Dictionary;

/**
 * @author Peter Nagy <peter.1.nagy@nokia.com>
 */
public interface Condition {
	boolean isEvaluated();
	boolean isMutable();
	boolean isSatisfied();
	boolean isSatisfied(Condition[] conds,Dictionary context);
}

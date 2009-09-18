/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.cases.condpermadmin.testcond;

import java.util.Dictionary;
import java.util.HashMap;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

public class TestPostPonedCondition implements Condition {

	private final String id;
	private final boolean mutable;
	private boolean curMutable;
	private final boolean postponed;
	private boolean curPostponed;
	private final boolean satisfied;
	private boolean curSatisfied;
	private final Bundle bundle;

	private static final HashMap conditionIDs = new HashMap();

	private TestPostPonedCondition(String id, boolean mutable, boolean postponed, boolean satisfied, Bundle bundle) {
		this.id = id;
		this.mutable = this.curMutable = mutable;
		this.postponed = this.curPostponed = postponed;
		this.satisfied = this.curSatisfied = satisfied;
		this.bundle = bundle;
	}

	static public Condition getCondition(final Bundle bundle, ConditionInfo info) {
		if (!TestPostPonedCondition.class.getName().equals(info.getType()))
			throw new IllegalArgumentException("ConditionInfo must be of type \"" + TestPostPonedCondition.class.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		String[] args = info.getArgs();
		if (args.length != 4)
			throw new IllegalArgumentException("Illegal number of args: " + args.length); //$NON-NLS-1$
		String identity = args[0] + '_' + bundle.getBundleId();
		boolean mut = Boolean.valueOf(args[1]).booleanValue();
		boolean post = Boolean.valueOf(args[2]).booleanValue();
		boolean sat = Boolean.valueOf(args[3]).booleanValue();;
		synchronized (conditionIDs) {
			TestPostPonedCondition condition = (TestPostPonedCondition) conditionIDs.get(identity);
			if (condition == null) {
				condition = new TestPostPonedCondition(identity, mut, post, sat, bundle);
				conditionIDs.put(identity, condition);
			}
			return condition;
		}
	}

	static public TestPostPonedCondition getTestCondition(String id) {
		synchronized (conditionIDs) {
			return (TestPostPonedCondition) conditionIDs.get(id);
		}
	}

	static public void clearConditions() {
		synchronized (conditionIDs) {
			conditionIDs.clear();
		}
	}

	public boolean isMutable() {
		return curMutable;
	}

	public boolean isPostponed() {
		return curPostponed;
	}

	public boolean isSatisfied() {
		return curSatisfied;
	}

	public boolean isSatisfied(Condition[] conditions, Dictionary context) {
		if (!isPostponed())
			throw new IllegalStateException("Should not call isSatisfied(Condition[] conditions, Dictionary context)"); //$NON-NLS-1$
		for (int i = 0; i < conditions.length; i++) {
			Boolean isSatisfied = (Boolean) context.get(conditions[i]);
			if (isSatisfied == null) {
				isSatisfied = new Boolean(conditions[i].isSatisfied());
				context.put(conditions[i], isSatisfied);
			}
			if (!isSatisfied.booleanValue())
				return false;
		}
		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof TestPostPonedCondition))
			return false;
		TestPostPonedCondition otherCondition = (TestPostPonedCondition) o;
		return id.equals(otherCondition.id) && postponed == otherCondition.postponed && satisfied == otherCondition.satisfied && mutable == otherCondition.mutable && bundle == otherCondition.bundle;
	}

	public void setMutable(boolean mutable) {
		this.curMutable = mutable;
	}

	public void setPostponed(boolean isPostponed) {
		this.curPostponed = isPostponed;
	}

	public void setSatisfied(boolean isSatisfied) {
		this.curSatisfied = isSatisfied;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public String toString() {
		return id + '-' + postponed + '-' + mutable + '-' + satisfied;
	}
}

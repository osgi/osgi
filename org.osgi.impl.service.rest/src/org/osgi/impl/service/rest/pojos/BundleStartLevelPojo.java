/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.rest.pojos;

import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.impl.service.rest.PojoReflector.RootNode;

/**
 * Pojo for the bundle start level.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
@RootNode(name = "bundleStartLevel")
public final class BundleStartLevelPojo {

	private int		startLevel;
	private boolean	activationPolicyUsed;
	private boolean	persistentlyStarted;

	public BundleStartLevelPojo(final BundleStartLevel sl) {
		this.startLevel = sl.getStartLevel();
		this.activationPolicyUsed = sl.isActivationPolicyUsed();
		this.persistentlyStarted = sl.isPersistentlyStarted();
	}

	public BundleStartLevelPojo() {

	}

	public int getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(final int sl) {
		this.startLevel = sl;
	}

	public boolean getActivationPolicyUsed() {
		return activationPolicyUsed;
	}

	public void setActivationPolicyUsed(boolean activationPolicyUsed) {
		this.activationPolicyUsed = activationPolicyUsed;
	}

	public boolean getPersistentlyStarted() {
		return persistentlyStarted;
	}

	public void setPersistentlyStarted(boolean persistentlyStarted) {
		this.persistentlyStarted = persistentlyStarted;
	}

	@Override
	public String toString() {
		return "Startlevel " + startLevel;
	}

}

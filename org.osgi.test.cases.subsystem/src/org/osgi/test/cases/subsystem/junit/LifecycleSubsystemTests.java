/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;


public class LifecycleSubsystemTests extends SubsystemTest{
	static final String subsystemFilter = "(objectClass=" + Subsystem.class.getName() + ")";
	public void test7A1_app_app() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_APPLICATION_A_S1);
	}

	public void test7A1_app_comp() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_APPLICATION_C_S1);
	}

	public void test7A1_app_feat() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_APPLICATION_F_S1);
	}

	public void test7A2_comp_app() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_COMPOSITE_A_S1);
	}

	public void test7A2_comp_comp() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_COMPOSITE_C_S1);
	}

	public void test7A2_comp_feat() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_COMPOSITE_F_S1);
	}

	public void test7A3_feat_app() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_FEATURE_A_S1);
	}

	public void test7A3_feat_comp() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_FEATURE_C_S1);
	}

	public void test7A3_feat_feat() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_FEATURE_F_S1);
	}

	private void doTest7A(String s1Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_NODEPS);
		Subsystem root = getRootSubsystem();

		Map<Long, LifecycleSubsystemTests_SL> sls = new HashMap<Long, LifecycleSubsystemTests_SL>();
		Map<Long, LifecycleSubsystemTests_BL> bls = new HashMap<Long, LifecycleSubsystemTests_BL>();
		LifecycleSubsystemTests_SL sl_root = new LifecycleSubsystemTests_SL(this, sls, bls);
		addServiceListener(root.getBundleContext(), sl_root, subsystemFilter);

		Subsystem c1 = doSubsystemInstall("install c1", root, "c1", SUBSYSTEM_6_EMPTY_COMPOSITE_A, false);


		ServiceRegistration<ResolverHookFactory> preventResolve = registerService(ResolverHookFactory.class, new PreventResolution(), null);
		Subsystem s1 = doSubsystemInstall("install s1", c1, "s1", s1Name, false);
		Subsystem s2 = s1.getChildren().iterator().next();
		preventResolve.unregister();

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);
		Bundle c = getBundle(s2, BUNDLE_NO_DEPS_C_V1);
		Bundle d = getBundle(s2, BUNDLE_NO_DEPS_D_V1);

		LifecycleSubsystemTests_BL bl_c1 = bls.get(c1.getSubsystemId());
		assertNotNull("bundle listener for c1 is null", bl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_c1.assertEvents(a, new BundleEvent(BundleEvent.INSTALLED, a));
			bl_c1.assertEvents(b, new BundleEvent(BundleEvent.INSTALLED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_c1.assertEvents(c, new BundleEvent(BundleEvent.INSTALLED, c));
				bl_c1.assertEvents(d, new BundleEvent(BundleEvent.INSTALLED, d));			
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_BL bl_s1 = bls.get(s1.getSubsystemId());
			assertNotNull("bundle listener for s1 is null", bl_s1);
			bl_s1.assertEvents(a, new BundleEvent(BundleEvent.INSTALLED, a));
			bl_s1.assertEvents(b, new BundleEvent(BundleEvent.INSTALLED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_s1.assertEvents(c, new BundleEvent(BundleEvent.INSTALLED, c));
				bl_s1.assertEvents(d, new BundleEvent(BundleEvent.INSTALLED, d));			
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_BL bl_s2 = bls.get(s2.getSubsystemId());
			assertNotNull("bundle listener for s2 is null", bl_s2);
			bl_s2.assertEvents(c, new BundleEvent(BundleEvent.INSTALLED, c));
			bl_s2.assertEvents(d, new BundleEvent(BundleEvent.INSTALLED, d));			
		}

		sl_root.assertEvents(
				new SubsystemEventInfo(State.INSTALLING, c1.getSubsystemId(), ServiceEvent.REGISTERED),
				new SubsystemEventInfo(State.INSTALLED, c1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.INSTALLING, s1.getSubsystemId(), ServiceEvent.REGISTERED),
				new SubsystemEventInfo(State.INSTALLING, s2.getSubsystemId(), ServiceEvent.REGISTERED),
				new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
		);

		LifecycleSubsystemTests_SL sl_c1 = sls.get(c1.getSubsystemId());
		assertNotNull("service listener for s1 is null", sl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, c1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLING, s1.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLING, s2.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		} else {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, c1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLING, s1.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_SL sl_s1 = sls.get(s1.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s1);
			sl_s1.assertEvents(
					new SubsystemEventInfo(State.INSTALLING, s2.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_SL sl_s2 = sls.get(s2.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s2);
			sl_s2.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}
	}

	public void test7B1_app_app() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_APPLICATION_A_S1);
	}

	public void test7B1_app_comp() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_APPLICATION_C_S1);
	}

	public void test7B1_app_feat() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_APPLICATION_F_S1);
	}

	public void test7B2_comp_app() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_COMPOSITE_A_S1);
	}

	public void test7B2_comp_comp() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_COMPOSITE_C_S1);
	}

	public void test7B2_comp_feat() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_COMPOSITE_F_S1);
	}

	public void test7B3_feat_app() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_FEATURE_A_S1);
	}

	public void test7B3_feat_comp() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_FEATURE_C_S1);
	}

	public void test7B3_feat_feat() throws InvalidSyntaxException {
		doTest7B(SUBSYSTEM_7_FEATURE_F_S1);
	}

	private void doTest7B(String s1Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_NODEPS);
		Subsystem root = getRootSubsystem();

		Map<Long, LifecycleSubsystemTests_SL> sls = new HashMap<Long, LifecycleSubsystemTests_SL>();
		Map<Long, LifecycleSubsystemTests_BL> bls = new HashMap<Long, LifecycleSubsystemTests_BL>();
		LifecycleSubsystemTests_SL sl_root = new LifecycleSubsystemTests_SL(this, sls, bls);
		addServiceListener(root.getBundleContext(), sl_root, subsystemFilter);

		Subsystem c1 = doSubsystemInstall("install c1", root, "c1", SUBSYSTEM_6_EMPTY_COMPOSITE_A, false);
		doSubsystemOperation("Start C1", c1, Operation.START, false);

		ServiceRegistration<ResolverHookFactory> preventResolve = registerService(ResolverHookFactory.class, new PreventResolution(), null);
		Subsystem s1 = doSubsystemInstall("install s1", c1, "s1", s1Name, false);
		Subsystem s2 = s1.getChildren().iterator().next();
		preventResolve.unregister();

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);
		Bundle c = getBundle(s2, BUNDLE_NO_DEPS_C_V1);
		Bundle d = getBundle(s2, BUNDLE_NO_DEPS_D_V1);

		clear(sl_root, sls, bls);
		doSubsystemOperation("Start S1", s1, Operation.START, false);

		LifecycleSubsystemTests_BL bl_c1 = bls.get(c1.getSubsystemId());
		assertNotNull("bundle listener for c1 is null", bl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_c1.assertEvents(a, new BundleEvent(BundleEvent.RESOLVED, a), new BundleEvent(BundleEvent.STARTING, a), new BundleEvent(BundleEvent.STARTED, a));
			bl_c1.assertEvents(b, new BundleEvent(BundleEvent.RESOLVED, b), new BundleEvent(BundleEvent.STARTING, b), new BundleEvent(BundleEvent.STARTED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_c1.assertEvents(c, new BundleEvent(BundleEvent.RESOLVED, c), new BundleEvent(BundleEvent.STARTING, c), new BundleEvent(BundleEvent.STARTED, c));
				bl_c1.assertEvents(d, new BundleEvent(BundleEvent.RESOLVED, d), new BundleEvent(BundleEvent.STARTING, d), new BundleEvent(BundleEvent.STARTED, d));				
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_BL bl_s1 = bls.get(s1.getSubsystemId());
			assertNotNull("bundle listener for s1 is null", bl_s1);
			bl_s1.assertEvents(a, new BundleEvent(BundleEvent.RESOLVED, a), new BundleEvent(BundleEvent.STARTING, a), new BundleEvent(BundleEvent.STARTED, a));
			bl_s1.assertEvents(b, new BundleEvent(BundleEvent.RESOLVED, b), new BundleEvent(BundleEvent.STARTING, b), new BundleEvent(BundleEvent.STARTED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_s1.assertEvents(c, new BundleEvent(BundleEvent.RESOLVED, c), new BundleEvent(BundleEvent.STARTING, c), new BundleEvent(BundleEvent.STARTED, c));
				bl_s1.assertEvents(d, new BundleEvent(BundleEvent.RESOLVED, d), new BundleEvent(BundleEvent.STARTING, d), new BundleEvent(BundleEvent.STARTED, d));			
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_BL bl_s2 = bls.get(s2.getSubsystemId());
			assertNotNull("bundle listener for s2 is null", bl_s2);
			bl_s2.assertEvents(c, new BundleEvent(BundleEvent.RESOLVED, c), new BundleEvent(BundleEvent.STARTING, c), new BundleEvent(BundleEvent.STARTED, c));
			bl_s2.assertEvents(d, new BundleEvent(BundleEvent.RESOLVED, d), new BundleEvent(BundleEvent.STARTING, d), new BundleEvent(BundleEvent.STARTED, d));			
		}

		sl_root.assertEvents(
				new SubsystemEventInfo(State.RESOLVING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.RESOLVING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.STARTING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.STARTING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.ACTIVE, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.ACTIVE, s1.getSubsystemId(), ServiceEvent.MODIFIED)
		);

		LifecycleSubsystemTests_SL sl_c1 = sls.get(c1.getSubsystemId());
		assertNotNull("service listener for s1 is null", sl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.RESOLVING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STARTING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STARTING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.ACTIVE, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.ACTIVE, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		} else {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.RESOLVING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STARTING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.ACTIVE, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_SL sl_s1 = sls.get(s1.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s1);
			sl_s1.assertEvents(
					new SubsystemEventInfo(State.RESOLVING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STARTING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STARTING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.ACTIVE, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.ACTIVE, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_SL sl_s2 = sls.get(s2.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s2);
			sl_s2.assertEvents(
					new SubsystemEventInfo(State.RESOLVING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STARTING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.ACTIVE, s2.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}
	}

	public void test7C1_app_app() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_APPLICATION_A_S1);
	}

	public void test7C1_app_comp() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_APPLICATION_C_S1);
	}

	public void test7C1_app_feat() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_APPLICATION_F_S1);
	}

	public void test7C2_comp_app() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_COMPOSITE_A_S1);
	}

	public void test7C2_comp_comp() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_COMPOSITE_C_S1);
	}

	public void test7C2_comp_feat() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_COMPOSITE_F_S1);
	}

	public void test7C3_feat_app() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_FEATURE_A_S1);
	}

	public void test7C3_feat_comp() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_FEATURE_C_S1);
	}

	public void test7C3_feat_feat() throws InvalidSyntaxException {
		doTest7C(SUBSYSTEM_7_FEATURE_F_S1);
	}

	private void doTest7C(String s1Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_NODEPS);
		Subsystem root = getRootSubsystem();

		Map<Long, LifecycleSubsystemTests_SL> sls = new HashMap<Long, LifecycleSubsystemTests_SL>();
		Map<Long, LifecycleSubsystemTests_BL> bls = new HashMap<Long, LifecycleSubsystemTests_BL>();
		LifecycleSubsystemTests_SL sl_root = new LifecycleSubsystemTests_SL(this, sls, bls);
		addServiceListener(root.getBundleContext(), sl_root, subsystemFilter);

		Subsystem c1 = doSubsystemInstall("install c1", root, "c1", SUBSYSTEM_6_EMPTY_COMPOSITE_A, false);
		doSubsystemOperation("Start C1", c1, Operation.START, false);

		ServiceRegistration<ResolverHookFactory> preventResolve = registerService(ResolverHookFactory.class, new PreventResolution(), null);
		Subsystem s1 = doSubsystemInstall("install s1", c1, "s1", s1Name, false);
		Subsystem s2 = s1.getChildren().iterator().next();
		preventResolve.unregister();

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);
		Bundle c = getBundle(s2, BUNDLE_NO_DEPS_C_V1);
		Bundle d = getBundle(s2, BUNDLE_NO_DEPS_D_V1);

		doSubsystemOperation("Start S1", s1, Operation.START, false);
		clear(sl_root, sls, bls);
		doSubsystemOperation("Stop S1", s1, Operation.STOP, false);

		LifecycleSubsystemTests_BL bl_c1 = bls.get(c1.getSubsystemId());
		assertNotNull("bundle listener for c1 is null", bl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_c1.assertEvents(a, new BundleEvent(BundleEvent.STOPPING, a), new BundleEvent(BundleEvent.STOPPED, a));
			bl_c1.assertEvents(b, new BundleEvent(BundleEvent.STOPPING, b), new BundleEvent(BundleEvent.STOPPED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_c1.assertEvents(c, new BundleEvent(BundleEvent.STOPPING, c), new BundleEvent(BundleEvent.STOPPED, c));
				bl_c1.assertEvents(d, new BundleEvent(BundleEvent.STOPPING, d), new BundleEvent(BundleEvent.STOPPED, d));				
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_BL bl_s1 = bls.get(s1.getSubsystemId());
			assertNotNull("bundle listener for s1 is null", bl_s1);
			bl_s1.assertEvents(a, new BundleEvent(BundleEvent.STOPPING, a), new BundleEvent(BundleEvent.STOPPED, a));
			bl_s1.assertEvents(b, new BundleEvent(BundleEvent.STOPPING, b), new BundleEvent(BundleEvent.STOPPED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_s1.assertEvents(c, new BundleEvent(BundleEvent.STOPPING, c), new BundleEvent(BundleEvent.STOPPED, c));
				bl_s1.assertEvents(d, new BundleEvent(BundleEvent.STOPPING, d), new BundleEvent(BundleEvent.STOPPED, d));			
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_BL bl_s2 = bls.get(s2.getSubsystemId());
			assertNotNull("bundle listener for s2 is null", bl_s2);
			bl_s2.assertEvents(c, new BundleEvent(BundleEvent.STOPPING, c), new BundleEvent(BundleEvent.STOPPED, c));
			bl_s2.assertEvents(d, new BundleEvent(BundleEvent.STOPPING, d), new BundleEvent(BundleEvent.STOPPED, d));			
		}

		sl_root.assertEvents(
				new SubsystemEventInfo(State.STOPPING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.STOPPING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
		);

		LifecycleSubsystemTests_SL sl_c1 = sls.get(c1.getSubsystemId());
		assertNotNull("service listener for s1 is null", sl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.STOPPING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STOPPING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		} else {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.STOPPING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_SL sl_s1 = sls.get(s1.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s1);
			sl_s1.assertEvents(
					new SubsystemEventInfo(State.STOPPING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.STOPPING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_SL sl_s2 = sls.get(s2.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s2);
			sl_s2.assertEvents(
					new SubsystemEventInfo(State.STOPPING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.RESOLVED, s2.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}
	}

	public void test7D1_app_app() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_APPLICATION_A_S1);
	}

	public void test7D1_app_comp() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_APPLICATION_C_S1);
	}

	public void test7D1_app_feat() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_APPLICATION_F_S1);
	}

	public void test7D2_comp_app() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_COMPOSITE_A_S1);
	}

	public void test7D2_comp_comp() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_COMPOSITE_C_S1);
	}

	public void test7D2_comp_feat() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_COMPOSITE_F_S1);
	}

	public void test7D3_feat_app() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_FEATURE_A_S1);
	}

	public void test7D3_feat_comp() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_FEATURE_C_S1);
	}

	public void test7D3_feat_feat() throws InvalidSyntaxException {
		doTest7D(SUBSYSTEM_7_FEATURE_F_S1);
	}

	private void doTest7D(String s1Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_NODEPS);
		Subsystem root = getRootSubsystem();

		Map<Long, LifecycleSubsystemTests_SL> sls = new HashMap<Long, LifecycleSubsystemTests_SL>();
		Map<Long, LifecycleSubsystemTests_BL> bls = new HashMap<Long, LifecycleSubsystemTests_BL>();
		LifecycleSubsystemTests_SL sl_root = new LifecycleSubsystemTests_SL(this, sls, bls);
		addServiceListener(root.getBundleContext(), sl_root, subsystemFilter);

		Subsystem c1 = doSubsystemInstall("install c1", root, "c1", SUBSYSTEM_6_EMPTY_COMPOSITE_A, false);
		doSubsystemOperation("Start C1", c1, Operation.START, false);

		ServiceRegistration<ResolverHookFactory> preventResolve = registerService(ResolverHookFactory.class, new PreventResolution(), null);
		Subsystem s1 = doSubsystemInstall("install s1", c1, "s1", s1Name, false);
		Subsystem s2 = s1.getChildren().iterator().next();
		preventResolve.unregister();

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);
		Bundle c = getBundle(s2, BUNDLE_NO_DEPS_C_V1);
		Bundle d = getBundle(s2, BUNDLE_NO_DEPS_D_V1);

		doSubsystemOperation("Start S1", s1, Operation.START, false);
		doSubsystemOperation("Stop S1", s1, Operation.STOP, false);
		clear(sl_root, sls, bls);
		doSubsystemOperation("Uninstall S1", s1, Operation.UNINSTALL, false);

		LifecycleSubsystemTests_BL bl_c1 = bls.get(c1.getSubsystemId());
		assertNotNull("bundle listener for c1 is null", bl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_c1.assertEvents(a, new BundleEvent(BundleEvent.UNRESOLVED, a), new BundleEvent(BundleEvent.UNINSTALLED, a));
			bl_c1.assertEvents(b, new BundleEvent(BundleEvent.UNRESOLVED, b), new BundleEvent(BundleEvent.UNINSTALLED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_c1.assertEvents(c, new BundleEvent(BundleEvent.UNRESOLVED, c), new BundleEvent(BundleEvent.UNINSTALLED, c));
				bl_c1.assertEvents(d, new BundleEvent(BundleEvent.UNRESOLVED, d), new BundleEvent(BundleEvent.UNINSTALLED, d));				
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_BL bl_s1 = bls.get(s1.getSubsystemId());
			assertNotNull("bundle listener for s1 is null", bl_s1);
			bl_s1.assertEvents(a, new BundleEvent(BundleEvent.UNRESOLVED, a), new BundleEvent(BundleEvent.UNINSTALLED, a));
			bl_s1.assertEvents(b, new BundleEvent(BundleEvent.UNRESOLVED, b), new BundleEvent(BundleEvent.UNINSTALLED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_s1.assertEvents(c, new BundleEvent(BundleEvent.UNRESOLVED, c), new BundleEvent(BundleEvent.UNINSTALLED, c));
				bl_s1.assertEvents(d, new BundleEvent(BundleEvent.UNRESOLVED, d), new BundleEvent(BundleEvent.UNINSTALLED, d));			
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_BL bl_s2 = bls.get(s2.getSubsystemId());
			assertNotNull("bundle listener for s2 is null", bl_s2);
			bl_s2.assertEvents(c, new BundleEvent(BundleEvent.UNRESOLVED, c), new BundleEvent(BundleEvent.UNINSTALLED, c));
			bl_s2.assertEvents(d, new BundleEvent(BundleEvent.UNRESOLVED, d), new BundleEvent(BundleEvent.UNINSTALLED, d));			
		}

		sl_root.assertEvents(
				new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.UNINSTALLING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.UNINSTALLING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.UNREGISTERING),
				new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
				new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.UNREGISTERING)
		);

		LifecycleSubsystemTests_SL sl_c1 = sls.get(c1.getSubsystemId());
		assertNotNull("service listener for s1 is null", sl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.UNREGISTERING),
					new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.UNREGISTERING)
			);
		} else {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.UNREGISTERING)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			LifecycleSubsystemTests_SL sl_s1 = sls.get(s1.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s1);
			sl_s1.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLING, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.UNREGISTERING),
					new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s1.getSubsystemId(), ServiceEvent.UNREGISTERING)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			LifecycleSubsystemTests_SL sl_s2 = sls.get(s2.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s2);
			sl_s2.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLING, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.UNINSTALLED, s2.getSubsystemId(), ServiceEvent.UNREGISTERING)
			);
		}
	}

	public void test7E1_app_app() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_APPLICATION_A_S1);
	}

	public void test7E1_app_comp() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_APPLICATION_C_S1);
	}

	public void test7E1_app_feat() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_APPLICATION_F_S1);
	}

	public void test7E2_comp_app() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_COMPOSITE_A_S1);
	}

	public void test7E2_comp_comp() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_COMPOSITE_C_S1);
	}

	public void test7E2_comp_feat() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_COMPOSITE_F_S1);
	}

	public void test7E3_feat_app() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_FEATURE_A_S1);
	}

	public void test7E3_feat_comp() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_FEATURE_C_S1);
	}

	public void test7E3_feat_feat() throws InvalidSyntaxException {
		doTest7E(SUBSYSTEM_7_ORDERED_FEATURE_F_S1);
	}

	private void doTest7E(String s1Name) throws InvalidSyntaxException {
		Subsystem root = getRootSubsystem();

		Map<Long, LifecycleSubsystemTests_SL> sls = new HashMap<Long, LifecycleSubsystemTests_SL>();
		Map<Long, LifecycleSubsystemTests_BL> bls = new HashMap<Long, LifecycleSubsystemTests_BL>();
		LifecycleSubsystemTests_SL sl_root = new LifecycleSubsystemTests_SL(this, sls, bls,
				BundleEvent.STARTING | BundleEvent.STARTED);
		addServiceListener(root.getBundleContext(), sl_root, subsystemFilter);

		Subsystem c1 = doSubsystemInstall("install c1", root, "c1", SUBSYSTEM_6_EMPTY_COMPOSITE_A, false);
		doSubsystemOperation("Start C1", c1, Operation.START, false);

		ServiceRegistration<ResolverHookFactory> preventResolve = registerService(ResolverHookFactory.class, new PreventResolution(), null);
		Subsystem s1 = doSubsystemInstall("install s1", c1, "s1", s1Name, false);
		Subsystem s2 = s1.getChildren().iterator().next();
		preventResolve.unregister();

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);
		Bundle c = getBundle(s1, BUNDLE_NO_DEPS_C_V1);
		Bundle d = getBundle(s2, BUNDLE_NO_DEPS_D_V1);
		Bundle e = getBundle(s2, BUNDLE_NO_DEPS_E_V1);
		Bundle f = getBundle(s2, BUNDLE_NO_DEPS_F_V1);

		clear(sl_root, sls, bls);
		doSubsystemOperation("Start S1", s1, Operation.START, false);

		LifecycleSubsystemTests_BL bl_c1 = bls.get(c1.getSubsystemId());
		assertNotNull("bundle listener for c1 is null", bl_c1);
		List<BundleEvent> expected = new ArrayList<BundleEvent>();
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			expected.add(new BundleEvent(BundleEvent.STARTING, b));
			expected.add(new BundleEvent(BundleEvent.STARTED, b));
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				expected.add(new BundleEvent(BundleEvent.STARTING, e));
				expected.add(new BundleEvent(BundleEvent.STARTED, e));
				expected.add(new BundleEvent(BundleEvent.STARTING, d));
				expected.add(new BundleEvent(BundleEvent.STARTED, d));
				expected.add(new BundleEvent(BundleEvent.STARTING, f));
				expected.add(new BundleEvent(BundleEvent.STARTED, f));
			}
			expected.add(new BundleEvent(BundleEvent.STARTING, c));
			expected.add(new BundleEvent(BundleEvent.STARTED, c));
			expected.add(new BundleEvent(BundleEvent.STARTING, a));
			expected.add(new BundleEvent(BundleEvent.STARTED, a));
		}
		bl_c1.assertEvents(expected.toArray(new BundleEvent[expected.size()]));

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			expected.clear();
			LifecycleSubsystemTests_BL bl_s1 = bls.get(s1.getSubsystemId());
			assertNotNull("bundle listener for s1 is null", bl_s1);

			expected.add(new BundleEvent(BundleEvent.STARTING, b));
			expected.add(new BundleEvent(BundleEvent.STARTED, b));
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				expected.add(new BundleEvent(BundleEvent.STARTING, e));
				expected.add(new BundleEvent(BundleEvent.STARTED, e));
				expected.add(new BundleEvent(BundleEvent.STARTING, d));
				expected.add(new BundleEvent(BundleEvent.STARTED, d));
				expected.add(new BundleEvent(BundleEvent.STARTING, f));
				expected.add(new BundleEvent(BundleEvent.STARTED, f));
			}
			expected.add(new BundleEvent(BundleEvent.STARTING, c));
			expected.add(new BundleEvent(BundleEvent.STARTED, c));
			expected.add(new BundleEvent(BundleEvent.STARTING, a));
			expected.add(new BundleEvent(BundleEvent.STARTED, a));

			bl_s1.assertEvents(expected.toArray(new BundleEvent[expected.size()]));
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			expected.clear();
			LifecycleSubsystemTests_BL bl_s2 = bls.get(s2.getSubsystemId());
			assertNotNull("bundle listener for s2 is null", bl_s2);
			expected.add(new BundleEvent(BundleEvent.STARTING, e));
			expected.add(new BundleEvent(BundleEvent.STARTED, e));
			expected.add(new BundleEvent(BundleEvent.STARTING, d));
			expected.add(new BundleEvent(BundleEvent.STARTED, d));
			expected.add(new BundleEvent(BundleEvent.STARTING, f));
			expected.add(new BundleEvent(BundleEvent.STARTED, f));
			bl_s2.assertEvents(expected.toArray(new BundleEvent[expected.size()]));
		}
	}

	public void test7F1_app_app() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_APPLICATION_A_S1);
	}

	public void test7F1_app_comp() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_APPLICATION_C_S1);
	}

	public void test7F1_app_feat() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_APPLICATION_F_S1);
	}

	public void test7F2_comp_app() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_COMPOSITE_A_S1);
	}

	public void test7F2_comp_comp() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_COMPOSITE_C_S1);
	}

	public void test7F2_comp_feat() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_COMPOSITE_F_S1);
	}

	public void test7F3_feat_app() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_FEATURE_A_S1);
	}

	public void test7F3_feat_comp() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_FEATURE_C_S1);
	}

	public void test7F3_feat_feat() throws InvalidSyntaxException {
		doTest7F(SUBSYSTEM_7_ORDERED_FEATURE_F_S1);
	}

	private void doTest7F(String s1Name) throws InvalidSyntaxException {
		Subsystem root = getRootSubsystem();

		Map<Long, LifecycleSubsystemTests_SL> sls = new HashMap<Long, LifecycleSubsystemTests_SL>();
		Map<Long, LifecycleSubsystemTests_BL> bls = new HashMap<Long, LifecycleSubsystemTests_BL>();
		LifecycleSubsystemTests_SL sl_root = new LifecycleSubsystemTests_SL(this, sls, bls,
				BundleEvent.STOPPING | BundleEvent.STOPPED);
		addServiceListener(root.getBundleContext(), sl_root, subsystemFilter);

		Subsystem c1 = doSubsystemInstall("install c1", root, "c1", SUBSYSTEM_6_EMPTY_COMPOSITE_A, false);
		doSubsystemOperation("Start C1", c1, Operation.START, false);

		ServiceRegistration<ResolverHookFactory> preventResolve = registerService(ResolverHookFactory.class, new PreventResolution(), null);
		Subsystem s1 = doSubsystemInstall("install s1", c1, "s1", s1Name, false);
		Subsystem s2 = s1.getChildren().iterator().next();
		preventResolve.unregister();

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);
		Bundle c = getBundle(s1, BUNDLE_NO_DEPS_C_V1);
		Bundle d = getBundle(s2, BUNDLE_NO_DEPS_D_V1);
		Bundle e = getBundle(s2, BUNDLE_NO_DEPS_E_V1);
		Bundle f = getBundle(s2, BUNDLE_NO_DEPS_F_V1);

		doSubsystemOperation("Start S1", s1, Operation.START, false);
		clear(sl_root, sls, bls);

		doSubsystemOperation("Start S1", s1, Operation.STOP, false);

		LifecycleSubsystemTests_BL bl_c1 = bls.get(c1.getSubsystemId());
		assertNotNull("bundle listener for c1 is null", bl_c1);
		List<BundleEvent> expected = new ArrayList<BundleEvent>();
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			expected.add(new BundleEvent(BundleEvent.STOPPING, a));
			expected.add(new BundleEvent(BundleEvent.STOPPED, a));
			expected.add(new BundleEvent(BundleEvent.STOPPING, c));
			expected.add(new BundleEvent(BundleEvent.STOPPED, c));
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				expected.add(new BundleEvent(BundleEvent.STOPPING, f));
				expected.add(new BundleEvent(BundleEvent.STOPPED, f));
				expected.add(new BundleEvent(BundleEvent.STOPPING, d));
				expected.add(new BundleEvent(BundleEvent.STOPPED, d));
				expected.add(new BundleEvent(BundleEvent.STOPPING, e));
				expected.add(new BundleEvent(BundleEvent.STOPPED, e));
			}
			expected.add(new BundleEvent(BundleEvent.STOPPING, b));
			expected.add(new BundleEvent(BundleEvent.STOPPED, b));
		}
		bl_c1.assertEvents(expected.toArray(new BundleEvent[expected.size()]));

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			expected.clear();
			LifecycleSubsystemTests_BL bl_s1 = bls.get(s1.getSubsystemId());
			assertNotNull("bundle listener for s1 is null", bl_s1);

			expected.add(new BundleEvent(BundleEvent.STOPPING, a));
			expected.add(new BundleEvent(BundleEvent.STOPPED, a));
			expected.add(new BundleEvent(BundleEvent.STOPPING, c));
			expected.add(new BundleEvent(BundleEvent.STOPPED, c));
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				expected.add(new BundleEvent(BundleEvent.STOPPING, f));
				expected.add(new BundleEvent(BundleEvent.STOPPED, f));
				expected.add(new BundleEvent(BundleEvent.STOPPING, d));
				expected.add(new BundleEvent(BundleEvent.STOPPED, d));
				expected.add(new BundleEvent(BundleEvent.STOPPING, e));
				expected.add(new BundleEvent(BundleEvent.STOPPED, e));
			}
			expected.add(new BundleEvent(BundleEvent.STOPPING, b));
			expected.add(new BundleEvent(BundleEvent.STOPPED, b));

			bl_s1.assertEvents(expected.toArray(new BundleEvent[expected.size()]));
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			expected.clear();
			LifecycleSubsystemTests_BL bl_s2 = bls.get(s2.getSubsystemId());
			assertNotNull("bundle listener for s2 is null", bl_s2);
			expected.add(new BundleEvent(BundleEvent.STOPPING, f));
			expected.add(new BundleEvent(BundleEvent.STOPPED, f));
			expected.add(new BundleEvent(BundleEvent.STOPPING, d));
			expected.add(new BundleEvent(BundleEvent.STOPPED, d));
			expected.add(new BundleEvent(BundleEvent.STOPPING, e));
			expected.add(new BundleEvent(BundleEvent.STOPPED, e));
			bl_s2.assertEvents(expected.toArray(new BundleEvent[expected.size()]));
		}
	}

	public void test7G1_app_app() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test7G1_app_comp() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test7G1_app_feat() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test7G2_comp_app() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test7G2_comp_comp() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test7G2_comp_feat() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test7G3_feat_app() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test7G3_feat_comp() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test7G3_feat_feat() throws InvalidSyntaxException {
		doTest7G(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	private void doTest7G(String s1Name, String s2Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_2);

		Subsystem root = getRootSubsystem();

		LifecycleSubsystemTests_BL bl_root = new LifecycleSubsystemTests_BL(BundleEvent.STARTING | BundleEvent.STARTED | BundleEvent.STOPPING | BundleEvent.STOPPED);
		root.getBundleContext().addBundleListener(bl_root);

		Subsystem s1 = doSubsystemInstall("install s1", root, "s1", s1Name, false);
		Subsystem s2 = doSubsystemInstall("install s2", root, "s2", s2Name, false);


		Bundle a = getBundle(root, BUNDLE_SHARE_A);
		Bundle b = getBundle(root, BUNDLE_SHARE_B);
		Bundle c = getBundle(s1, BUNDLE_SHARE_C);
		Bundle d = getBundle(s2, BUNDLE_SHARE_D);
		Bundle e = getBundle(s2, BUNDLE_SHARE_E);

		bl_root.clear();

		doSubsystemOperation("Start S1", s1, Operation.START, false);
		doSubsystemOperation("Start S2", s2, Operation.START, false);
		
		bl_root.assertEvents(a, new BundleEvent(BundleEvent.STARTING, a), new BundleEvent(BundleEvent.STARTED, a));
		bl_root.assertEvents(b, new BundleEvent(BundleEvent.STARTING, b), new BundleEvent(BundleEvent.STARTED, b));
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_root.assertEvents(c, new BundleEvent(BundleEvent.STARTING, c), new BundleEvent(BundleEvent.STARTED, c));
		}
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			bl_root.assertEvents(d, new BundleEvent(BundleEvent.STARTING, d), new BundleEvent(BundleEvent.STARTED, d));
			bl_root.assertEvents(e, new BundleEvent(BundleEvent.STARTING, e), new BundleEvent(BundleEvent.STARTED, e));
		}
		bl_root.clear();

		doSubsystemOperation("Stop S1", s1, Operation.STOP, false);
		doSubsystemOperation("Start S1", s1, Operation.START, false);

		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_root.assertEvents(c, new BundleEvent(BundleEvent.STOPPING, c), new BundleEvent(BundleEvent.STOPPED, c), new BundleEvent(BundleEvent.STARTING, c), new BundleEvent(BundleEvent.STARTED, c));
		}
		bl_root.clear();

		doSubsystemOperation("Stop S2", s2, Operation.STOP, false);
		doSubsystemOperation("Start S2", s2, Operation.START, false);

		bl_root.assertEvents(b, new BundleEvent(BundleEvent.STOPPING, b), new BundleEvent(BundleEvent.STOPPED, b), new BundleEvent(BundleEvent.STARTING, b), new BundleEvent(BundleEvent.STARTED, b));
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			bl_root.assertEvents(d, new BundleEvent(BundleEvent.STOPPING, d), new BundleEvent(BundleEvent.STOPPED, d), new BundleEvent(BundleEvent.STARTING, d), new BundleEvent(BundleEvent.STARTED, d));
			bl_root.assertEvents(e, new BundleEvent(BundleEvent.STOPPING, e), new BundleEvent(BundleEvent.STOPPED, e), new BundleEvent(BundleEvent.STARTING, e), new BundleEvent(BundleEvent.STARTED, e));
		}

		bl_root.clear();
	
		doSubsystemOperation("Stop S1", s1, Operation.STOP, false);
		doSubsystemOperation("Stop S2", s2, Operation.STOP, false);

		bl_root.assertEvents(a,new BundleEvent(BundleEvent.STOPPING, a), new BundleEvent(BundleEvent.STOPPED, a));
		bl_root.assertEvents(b,new BundleEvent(BundleEvent.STOPPING, b), new BundleEvent(BundleEvent.STOPPED, b));
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_root.assertEvents(c, new BundleEvent(BundleEvent.STOPPING, c), new BundleEvent(BundleEvent.STOPPED, c));
		}
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			bl_root.assertEvents(d, new BundleEvent(BundleEvent.STOPPING, d), new BundleEvent(BundleEvent.STOPPED, d));
			bl_root.assertEvents(e, new BundleEvent(BundleEvent.STOPPING, e), new BundleEvent(BundleEvent.STOPPED, e));
		}

		bl_root.clear();
	}


	private void clear(LifecycleSubsystemTests_SL sl_root, Map<Long, LifecycleSubsystemTests_SL> sls, Map<Long, LifecycleSubsystemTests_BL> bls) {
		sl_root.clear();
		for (LifecycleSubsystemTests_SL sl : sls.values()) {
			sl.clear();
		}
		for (LifecycleSubsystemTests_BL bl : bls.values()) {
			bl.clear();
		}
	}
}

/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;


public class LifecycleSubsystemTests extends SubsystemTest{
	static final String subsystemFilter = "(objectClass=" + Subsystem.class.getName() + ")";
	public static class BL implements SynchronousBundleListener{
		Map<Bundle, List<BundleEvent>> events = new HashMap<Bundle, List<BundleEvent>>();
		public void bundleChanged(BundleEvent event) {
			synchronized (events) {
				List<BundleEvent> e = events.get(event.getBundle());
				if (e == null) {
					e = new ArrayList<BundleEvent>();
					events.put(event.getBundle(), e);
				}
				e.add(event);
			}
		}
		public void assertEvents(Bundle b, BundleEvent... expected) {
			List<BundleEvent> current;
			synchronized (events) {
				current = events.get(b);
				current = current == null ? new ArrayList<BundleEvent>() : new ArrayList<BundleEvent>(current);
			}
			Assert.assertEquals("Wrong number of bundle events:" + b, expected.length, current.size());
			for (int i = 0; i < expected.length; i++) {
				assertEquals(i, expected[i], current.get(i));
			}
		}

		private void assertEquals(int index, BundleEvent expected, BundleEvent actual) {
			Assert.assertEquals("Wrong bundle for event: " + index, expected.getBundle(), actual.getBundle());
			Assert.assertEquals("Wrong bundle event type: " + index, expected.getType(), actual.getType());
		}

		public void clear() {
			synchronized (events) {
				events.clear();
			}
		}
	}

	public static class SubsystemEventInfo {
		final Subsystem.State state;
		final Long subsystemID;
		final int eventType;

		public SubsystemEventInfo(ServiceEvent event) {
			this.state = (State) event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_STATE_PROPERTY);
			this.eventType = event.getType();
			this.subsystemID = (Long) extractRefernce(event).getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY);
		}

		public SubsystemEventInfo(State state, Long id, int eventType) {
			this.state = state;
			this.subsystemID = id;
			this.eventType = eventType;
		}

		@SuppressWarnings("unchecked")
		private ServiceReference<Subsystem> extractRefernce(ServiceEvent event) {
			return (ServiceReference<Subsystem>) event.getServiceReference();
		}
	}

	public class SL2 implements ServiceListener {
		final Map<Long, SL2> sl2s;
		final Map<Long, BL> bls;
		final List<SubsystemEventInfo> events = new ArrayList<SubsystemEventInfo>();

		public SL2(Map<Long, SL2> sl2s, Map<Long, BL> bls) {
			this.sl2s = sl2s;
			this.bls = bls;
		}

		public void assertEvents(SubsystemEventInfo... expected) {
			List<SubsystemEventInfo> current;
			synchronized (events) {
				current = new ArrayList<SubsystemEventInfo>(events);
			}
			Assert.assertEquals("Wrong number of service events.", expected.length, current.size());
			for (int i = 0; i < expected.length; i++) {
				assertEquals(i, expected[i], current.get(i));
			}
		}

		private void assertEquals(int index, SubsystemEventInfo expected, SubsystemEventInfo actual) {
			Assert.assertEquals("Wrong subsystem state: " + index, expected.state, actual.state);
			Assert.assertEquals("Wrong subsystem id: " + index, expected.subsystemID, actual.subsystemID);
			Assert.assertEquals("Wrong service event type: " + index, expected.eventType, actual.eventType);
		}

		public void clear() {
			synchronized (events) {
				events.clear();	
			}
		}

		public void serviceChanged(ServiceEvent event) {
			if (event.getType() == ServiceEvent.REGISTERED) {
				if (Subsystem.State.INSTALLING.equals(event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_STATE_PROPERTY))){
					String type = (String) event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_TYPE_PROPERTY);
					if (SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION.equals(type) || SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE.equals(type)) {
						if (!sl2s.containsKey(event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY))) {
							SL2 sl2 = new SL2(sl2s, bls);
							BL bl = new BL();
							Long id = (Long) event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY);
							sl2s.put(id, sl2);
							bls.put(id, bl);
							Subsystem subsystem = (Subsystem) getContext().getService(event.getServiceReference());
							addServiceListener(subsystem.getBundleContext(), sl2, subsystemFilter);
							addBundleListener(subsystem.getBundleContext(), bl);
							getContext().ungetService(event.getServiceReference());
						}
					}
				}
			}
			synchronized (events) {
				events.add(new SubsystemEventInfo(event));
			}
		}
		
	}

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

	public void test7A2_feat_app() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_FEATURE_A_S1);
	}

	public void test7A2_feat_comp() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_FEATURE_C_S1);
	}

	public void test7A2_feat_feat() throws InvalidSyntaxException {
		doTest7A(SUBSYSTEM_7_FEATURE_F_S1);
	}

	private void doTest7A(String s1Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_NODEPS);
		Subsystem root = getRootSubsystem();

		Map<Long, SL2> sls = new HashMap<Long, SL2>();
		Map<Long, BL> bls = new HashMap<Long, BL>();
		SL2 sl_root = new SL2(sls, bls);
		root.getBundleContext().addServiceListener(sl_root, subsystemFilter);

		Subsystem c1 = doSubsystemInstall("install c1", root, "c1", SUBSYSTEM_6_EMPTY_COMPOSITE_A, false);



		Subsystem s1 = doSubsystemInstall("install s1", c1, "s1", s1Name, false);
		Subsystem s2 = s1.getChildren().iterator().next();

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);
		Bundle c = getBundle(s2, BUNDLE_NO_DEPS_C_V1);
		Bundle d = getBundle(s2, BUNDLE_NO_DEPS_D_V1);

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			BL bl_s1 = bls.get(s1.getSubsystemId());
			assertNotNull("bundle listener for s1 is null", bl_s1);
			bl_s1.assertEvents(a, new BundleEvent(BundleEvent.INSTALLED, a));
			bl_s1.assertEvents(b, new BundleEvent(BundleEvent.INSTALLED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_s1.assertEvents(c, new BundleEvent(BundleEvent.INSTALLED, c));
				bl_s1.assertEvents(d, new BundleEvent(BundleEvent.INSTALLED, d));			
			}
		}

		BL bl_c1 = bls.get(c1.getSubsystemId());
		assertNotNull("bundle listener for c1 is null", bl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			bl_c1.assertEvents(a, new BundleEvent(BundleEvent.INSTALLED, a));
			bl_c1.assertEvents(b, new BundleEvent(BundleEvent.INSTALLED, b));	
			if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
				bl_c1.assertEvents(c, new BundleEvent(BundleEvent.INSTALLED, c));
				bl_c1.assertEvents(d, new BundleEvent(BundleEvent.INSTALLED, d));			
			}
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			BL bl_s2 = bls.get(s2.getSubsystemId());
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

		SL2 sl_c1 = sls.get(c1.getSubsystemId());
		assertNotNull("service listener for s1 is null", sl_c1);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.INSTALLING, s1.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLING, s2.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		} else {
			sl_c1.assertEvents(
					new SubsystemEventInfo(State.INSTALLING, s1.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s1.getType())) {
			SL2 sl_s1 = sls.get(s1.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s1);
			sl_s1.assertEvents(
					new SubsystemEventInfo(State.INSTALLING, s2.getSubsystemId(), ServiceEvent.REGISTERED),
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED),
					new SubsystemEventInfo(State.INSTALLED, s1.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}

		if (!SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(s2.getType())) {
			SL2 sl_s2 = sls.get(s2.getSubsystemId());
			assertNotNull("service listener for s1 is null", sl_s2);
			sl_s2.assertEvents(
					new SubsystemEventInfo(State.INSTALLED, s2.getSubsystemId(), ServiceEvent.MODIFIED)
			);
		}
	}
}

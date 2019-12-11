/*
 * Copyright (c) OSGi Alliance (2012, 2017). All Rights Reserved.
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

package org.osgi.test.cases.framework.junit.dto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.osgi.dto.DTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.FrameworkDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;
import org.osgi.framework.wiring.dto.BundleWireDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;
import org.osgi.framework.wiring.dto.FrameworkWiringDTO;
import org.osgi.resource.dto.CapabilityDTO;
import org.osgi.resource.dto.CapabilityRefDTO;
import org.osgi.resource.dto.RequirementDTO;
import org.osgi.resource.dto.RequirementRefDTO;
import org.osgi.resource.dto.WireDTO;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.wiring.Wiring;

public class FrameworkDTOTestCase extends OSGiTestCase {

    private Bundle        tb1;
	private Bundle							tb2;
	private Set<BundleRevisionDTO>			resources;
	private Set<BundleRevisionDTO>			resourcesUntested;
	private Set<BundleWiringDTO.NodeDTO>	nodes;
	private Set<BundleWiringDTO.NodeDTO>	nodesUntested;

	@Override
	protected void setUp() throws Exception {
		resources = null;
		resourcesUntested = null;
		nodes = null;
		nodesUntested = null;
        tb1 = install("dto.tb1.jar");
		tb2 = install("dto.tb2.jar");
    }

	@Override
    protected void tearDown() throws Exception {
        if ((tb1.getState() & Bundle.UNINSTALLED) != Bundle.UNINSTALLED) {
            tb1.uninstall();
        }
		if ((tb2.getState() & Bundle.UNINSTALLED) != Bundle.UNINSTALLED) {
			tb2.uninstall();
		}
		Wiring.synchronousRefreshBundles(getContext(), tb1, tb2);
    }

    public void testBundleDTO() throws Exception {
        BundleDTO dto = tb1.adapt(BundleDTO.class);
        assertBundleDTO(tb1, dto);
        tb1.start();
        assertFalse("Bundle state matches", tb1.getState() == dto.state);
        dto = tb1.adapt(BundleDTO.class);
        assertBundleDTO(tb1, dto);
    }

    private void assertBundleDTO(Bundle bundle, BundleDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
        assertEquals("Bundle id does not match", bundle.getBundleId(), dto.id);
        assertEquals("Bundle lastModified does not match", bundle.getLastModified(), dto.lastModified);
        assertEquals("Bundle state does not match", bundle.getState(), dto.state);
        assertEquals("Bundle symbolicName does not match", bundle.getSymbolicName(), dto.symbolicName);
		assertEquals("Bundle versionField does not match", bundle.getVersion(),
				Version.valueOf(dto.version));
    }

    public void testBundleStartLevelDTO() throws Exception {
        BundleStartLevel bsl = tb1.adapt(BundleStartLevel.class);
        BundleStartLevelDTO dto = tb1.adapt(BundleStartLevelDTO.class);
		assertBundleStartLevelDTO(tb1, bsl, dto);
        bsl.setStartLevel(10);
        tb1.start(Bundle.START_ACTIVATION_POLICY);
        assertFalse("Bundle startLevel matches", bsl.getStartLevel() == dto.startLevel);
        assertFalse("Bundle activationPolicyUsed matches", bsl.isActivationPolicyUsed() == dto.activationPolicyUsed);
        assertFalse("Bundle persistentlyStarted matches", bsl.isPersistentlyStarted() == dto.persistentlyStarted);
        dto = tb1.adapt(BundleStartLevelDTO.class);
		assertBundleStartLevelDTO(tb1, bsl, dto);
    }

	private void assertBundleStartLevelDTO(Bundle b, BundleStartLevel bsl, BundleStartLevelDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		assertEquals("Bundle id does not match", b.getBundleId(), dto.bundle);
        assertEquals("Bundle startLevel does not match", bsl.getStartLevel(), dto.startLevel);
        assertEquals("Bundle activationPolicyUsed does not match", bsl.isActivationPolicyUsed(), dto.activationPolicyUsed);
        assertEquals("Bundle persistentlyStarted does not match", bsl.isPersistentlyStarted(), dto.persistentlyStarted);
    }

    public void testBundleRevisionDTO() throws Exception {
        tb1.start();
        BundleRevision revision = tb1.adapt(BundleRevision.class);
        BundleRevisionDTO dto = tb1.adapt(BundleRevisionDTO.class);
		assertTopBundleRevisionDTO(revision, dto);
		tb1.uninstall();
		assertEquals(Bundle.UNINSTALLED, tb1.getState());
		dto = tb1.adapt(BundleRevisionDTO.class);
		assertNull("Current BundleRevisionDTO for uninstalled bundle is not null", dto);
	}

	private void assertTopBundleRevisionDTO(BundleRevision revision, BundleRevisionDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		resources = new HashSet<BundleRevisionDTO>();
		resources.add(dto);
		resources = Collections.unmodifiableSet(resources);
		resourcesUntested = new HashSet<BundleRevisionDTO>(resources);
		nodes = Collections.unmodifiableSet(new HashSet<BundleWiringDTO.NodeDTO>());
		nodesUntested = new HashSet<BundleWiringDTO.NodeDTO>(nodes);
        assertBundleRevisionDTO(revision, dto);
		assertEquals("Too many resources", Collections.EMPTY_SET, resourcesUntested);
		assertEquals("Too many wiring nodes", Collections.EMPTY_SET, nodesUntested);
    }

    private BundleRevisionDTO getBundleRevisionDTObyId(int id) {
		assertNotNull("resources not set", resources);
		for (BundleRevisionDTO dto : resources) {
			if (dto.id == id) {
				return dto;
			}
		}
		fail("resource id not found: " + id);
		return null;
	}

	private void assertBundleRevisionDTO(BundleRevision revision, BundleRevisionDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		if (!resourcesUntested.remove(dto)) {
			return;
		}
		assertEquals("Bundle id does not match", revision.getBundle().getBundleId(), dto.bundle);
        assertEquals("BundleRevision symbolicName does not match", revision.getSymbolicName(), dto.symbolicName);
		assertEquals("BundleRevision versionField does not match",
				revision.getVersion(), Version.valueOf(dto.version));
        assertEquals("BundleRevision type does not match", revision.getTypes(), dto.type);
		assertListCapabilityDTO(revision.getDeclaredCapabilities(null), dto.capabilities);
		assertListRequirementDTO(revision.getDeclaredRequirements(null), dto.requirements);
    }

	private void assertListCapabilityDTO(List<BundleCapability> caps, List<CapabilityDTO> dtos) throws Exception {
        assertEquals("BundleCapability count does not match", caps.size(), dtos.size());
        for (int i = 0; i < caps.size(); i++) {
            BundleCapability cap = caps.get(i);
            CapabilityDTO dto = dtos.get(i);
			assertCapabilityDTO(cap, dto);
        }
    }

	private void assertCapabilityDTO(BundleCapability cap, CapabilityDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		assertBundleRevisionDTO(cap.getRevision(), getBundleRevisionDTObyId(dto.resource));
	    assertEquals("BundleCapability namespace does not match", cap.getNamespace(), dto.namespace);
		assertAttributesDTO("BundleCapability attributes does not match", cap.getAttributes(), dto.attributes);
	    assertEquals("BundleCapability directives does not match", cap.getDirectives(), dto.directives);
	}

	private void assertListCapabilityRefDTO(List<BundleCapability> caps, List<CapabilityRefDTO> dtos) throws Exception {
		assertEquals("BundleCapability count does not match", caps.size(), dtos.size());
		for (int i = 0; i < caps.size(); i++) {
			BundleCapability cap = caps.get(i);
			CapabilityRefDTO dto = dtos.get(i);
			assertCapabilityRefDTO(cap, dto);
		}
	}

	private void assertCapabilityRefDTO(BundleCapability cap, CapabilityRefDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		BundleRevisionDTO revisionDTO = getBundleRevisionDTObyId(dto.resource);
		assertBundleRevisionDTO(cap.getRevision(), revisionDTO);
		for (CapabilityDTO capDTO : revisionDTO.capabilities) {
			if (capDTO.id == dto.capability) {
				assertCapabilityDTO(cap, capDTO);
				return;
			}
		}
		fail("capability id not found: " + dto.capability);
	}

	private void assertAttributesDTO(String message, Map<String, Object> attributes, Map<String, Object> dto) throws Exception {
		assertEquals(message, attributes.size(), dto.size());
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Version) {
				value = String.valueOf(value);
			} else
				if (value instanceof List) {
					List<Object> newList = new ArrayList<Object>((List<?>) value);
					for (ListIterator<Object> iter = newList.listIterator(); iter.hasNext();) {
						Object element = iter.next();
						if (element instanceof Version) {
							iter.set(String.valueOf(element));
						}
					}
					value = newList;
				}
			assertEquals(message, value, dto.get(entry.getKey()));
		}
	}

	private void assertListRequirementDTO(List<BundleRequirement> reqs, List<RequirementDTO> dtos) throws Exception {
        assertEquals("BundleRequirements count does not match", reqs.size(), dtos.size());
        for (int i = 0; i < reqs.size(); i++) {
            BundleRequirement req = reqs.get(i);
            RequirementDTO dto = dtos.get(i);
			assertRequirementDTO(req, dto);
        }
    }

	private void assertRequirementDTO(BundleRequirement req, RequirementDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		assertBundleRevisionDTO(req.getRevision(), getBundleRevisionDTObyId(dto.resource));
	    assertEquals("BundleRequirement namespace does not match", req.getNamespace(), dto.namespace);
		assertAttributesDTO("BundleRequirement attributes does not match", req.getAttributes(), dto.attributes);
	    assertEquals("BundleRequirement directives does not match", req.getDirectives(), dto.directives);
	}

	private void assertListRequirementRefDTO(List<BundleRequirement> reqs, List<RequirementRefDTO> dtos) throws Exception {
		assertEquals("BundleRequirements count does not match", reqs.size(), dtos.size());
		for (int i = 0; i < reqs.size(); i++) {
			BundleRequirement req = reqs.get(i);
			RequirementRefDTO dto = dtos.get(i);
			assertRequirementRefDTO(req, dto);
		}
	}

	private void assertRequirementRefDTO(BundleRequirement req, RequirementRefDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		BundleRevisionDTO revisionDTO = getBundleRevisionDTObyId(dto.resource);
		assertBundleRevisionDTO(req.getRevision(), revisionDTO);
		for (RequirementDTO reqDTO : revisionDTO.requirements) {
			if (reqDTO.id == dto.requirement) {
				assertRequirementDTO(req, reqDTO);
				return;
			}
		}
		fail("requirement id not found: " + dto.requirement);
	}

	public void testArrayBundleRevisionDTO() throws Exception {
        tb1.start();
		tb1.update(entryStream("dto.tb1.jar"));
        BundleRevisions revisions = tb1.adapt(BundleRevisions.class);
		BundleRevisionDTO[] dtos = tb1.adapt(BundleRevisionDTO[].class);
		assertNotNull("dtos is null", dtos);
		List<BundleRevision> revs = revisions.getRevisions();
		final int size = revs.size();
		assertEquals("Revision count does not match", size, dtos.length);
		for (int i = 0; i < size; i++) {
			assertTopBundleRevisionDTO(revs.get(i), dtos[i]);
		}
        tb1.uninstall();
        assertEquals(Bundle.UNINSTALLED, tb1.getState());
		dtos = tb1.adapt(BundleRevisionDTO[].class);
		assertNull("Current BundleRevisionDTO[] for uninstalled bundle is not null", dtos);
    }

    public void testBundleWiringDTO() throws Exception {
        tb1.start();
        BundleWiring wiring = tb1.adapt(BundleWiring.class);
        BundleWiringDTO dto = tb1.adapt(BundleWiringDTO.class);
        assertBundleWiringDTO(wiring, dto);
        tb1.uninstall();
        assertEquals(Bundle.UNINSTALLED, tb1.getState());
        dto = tb1.adapt(BundleWiringDTO.class);
        assertNull("Current BundleWiringDTO for uninstalled bundle is not null", dto);
    }

    private void assertBundleWiringDTO(BundleWiring wiring, BundleWiringDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		assertEquals("Bundle id does not match", wiring.getBundle().getBundleId(), dto.bundle);
		resources = Collections.unmodifiableSet(new HashSet<BundleRevisionDTO>(dto.resources));
		resourcesUntested = new HashSet<BundleRevisionDTO>(resources);
		nodes = Collections.unmodifiableSet(new HashSet<BundleWiringDTO.NodeDTO>(dto.nodes));
		nodesUntested = new HashSet<BundleWiringDTO.NodeDTO>(nodes);
		assertBundleWiringNodeDTO(wiring, getBundleWiringNodeDTObyId(dto.root));
		assertEquals("Too many resources", Collections.EMPTY_SET, resourcesUntested);
		assertEquals("Too many wiring nodes", Collections.EMPTY_SET, nodesUntested);
	}

	private void assertFrameworkWiringDTO(Set<BundleWiring> allWirings,
			FrameworkWiringDTO dto, Map<BundleRevision,Integer> revisionIds)
			throws Exception {
		assertNotNull("dto is null", dto);
		assertEquals("Wrong number of dto nodes", allWirings.size(),
				dto.wirings.size());

		resources = Collections.unmodifiableSet(new HashSet<>(dto.resources));
		resourcesUntested = new HashSet<>(resources);
		nodes = Collections.unmodifiableSet(new HashSet<>(dto.wirings));
		nodesUntested = new HashSet<>(nodes);

		for (BundleWiringDTO.NodeDTO node : nodes) {
			BundleWiring wiring = findWiring(node, allWirings, revisionIds);
			assertBundleWiringNodeDTO(wiring, node);
		}
		assertEquals("Too many resources", Collections.EMPTY_SET, resourcesUntested);
		assertEquals("Too many wiring nodes", Collections.EMPTY_SET, nodesUntested);
    }

	private BundleWiring findWiring(BundleWiringDTO.NodeDTO node,
			Collection<BundleWiring> wirings,
			Map<BundleRevision,Integer> revisionIds) throws Exception {
		for (BundleWiring wiring : wirings) {
			Integer wiringRevisionId = revisionIds.get(wiring.getRevision());
			assertNotNull("Could not find revision id for wiring: "
					+ wiring.getRevision(), wiringRevisionId);
			if (node.resource == wiringRevisionId.intValue()) {
				return wiring;
			}
		}
		fail("No wiring found for node: " + node);
		return null;
	}

	private BundleWiringDTO.NodeDTO getBundleWiringNodeDTObyId(int id) {
		assertNotNull("wiring nodes not set", nodes);
		for (BundleWiringDTO.NodeDTO dto : nodes) {
			if (dto.id == id) {
				return dto;
			}
		}
		fail("wiring node id not found: " + id);
		return null;
	}

	private void assertBundleWiringNodeDTO(BundleWiring wiring, BundleWiringDTO.NodeDTO dto) throws Exception {
		assertNotNull("wiring node is null", dto);
		if (!nodesUntested.remove(dto)) {
			return;
		}
        assertEquals("BundleWiring isCurrent does not match", wiring.isCurrent(), dto.current);
		assertEquals("BundleWiring isUse does not match", wiring.isInUse(), dto.inUse);
		assertBundleRevisionDTO(wiring.getRevision(), getBundleRevisionDTObyId(dto.resource));
		assertListCapabilityRefDTO(wiring.getCapabilities(null), dto.capabilities);
		assertListRequirementRefDTO(wiring.getRequirements(null), dto.requirements);
        assertListBundleWireDTO(wiring.getProvidedWires(null), dto.providedWires);
        assertListBundleWireDTO(wiring.getRequiredWires(null), dto.requiredWires);
    }

	private void assertListBundleWireDTO(List<BundleWire> wires, List<WireDTO> dtos) throws Exception {
        assertEquals("BundleWire count does not match", wires.size(), dtos.size());
        for (int i = 0; i < wires.size(); i++) {
            BundleWire wire = wires.get(i);
            BundleWireDTO dto = (BundleWireDTO) dtos.get(i);
            assertBundleWireDTO(wire, dto);
        }
    }

    private void assertBundleWireDTO(BundleWire wire, BundleWireDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
		assertBundleWiringNodeDTO(wire.getProviderWiring(), getBundleWiringNodeDTObyId(dto.providerWiring));
		assertBundleWiringNodeDTO(wire.getRequirerWiring(), getBundleWiringNodeDTObyId(dto.requirerWiring));
		assertCapabilityRefDTO(wire.getCapability(), dto.capability);
		assertRequirementRefDTO(wire.getRequirement(), dto.requirement);
		assertBundleRevisionDTO(wire.getProvider(), getBundleRevisionDTObyId(dto.provider));
		assertBundleRevisionDTO(wire.getRequirer(), getBundleRevisionDTObyId(dto.requirer));
    }

	public void testArrayBundleWiringDTO() throws Exception {
        tb1.start();
		tb1.update(entryStream("dto.tb1.jar"));
        BundleRevisions revisions = tb1.adapt(BundleRevisions.class);
        List<BundleRevision> revs = revisions.getRevisions();
        final int size = revs.size();
        List<BundleWiring> wirings = new ArrayList<BundleWiring>(size);
        for (int i = 0; i < size; i++) {
            wirings.add(revs.get(i).getWiring());
        }
		BundleWiringDTO[] dtos = tb1.adapt(BundleWiringDTO[].class);
		assertBundleWiringDTOs(wirings, dtos);
        tb1.uninstall();
        assertEquals(Bundle.UNINSTALLED, tb1.getState());
		dtos = tb1.adapt(BundleWiringDTO[].class);
		assertNull("Current BundleWiringDTO[] for uninstalled bundle is not null", dtos);
    }

	private void assertBundleWiringDTOs(List<BundleWiring> wirings, BundleWiringDTO[] dtos) throws Exception {
		assertNotNull("dtos is null", dtos);
        final int size = wirings.size();
		assertEquals("Wiring count does not match", size, dtos.length);
        for (int i = 0; i < size; i++) {
			assertBundleWiringDTO(wirings.get(i), dtos[i]);
        }
    }

    public void testArrayServiceReferenceDTO() throws Exception {
        tb1.start();
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("dtotest", getName());
        Version versionProp = new Version("1.2.3.Q");
        properties.put("testVersion", versionProp);
        properties.put("testNumber", Double.valueOf(3.14));
        properties.put("testBoolean", Boolean.TRUE);
        properties.put("testCharacter", Character.valueOf('#'));
        TestDTO dtoProp = new TestDTO();
        dtoProp.field = "prop";
        properties.put("testDTO", dtoProp);
        Map<String, Object> mapProp = new HashMap<String, Object>();
        mapProp.put("mapProp", dtoProp);
        properties.put("testMap", mapProp);
        List<Object> listProp = new ArrayList<Object>();
        listProp.add(dtoProp);
        properties.put("testList", listProp);
        Set<Object> setProp = new HashSet<Object>();
        setProp.add(dtoProp);
        properties.put("testSet", setProp);
        Object[] objectArrayProp = new Object[] {dtoProp, versionProp};
        properties.put("testObjectArray", objectArrayProp);
        double[] doubleArrayProp = new double[] {3.14};
        properties.put("testdoubleArray", doubleArrayProp);
        Version[] versionArray = new Version[] {versionProp};
        properties.put("testVersionArray", versionArray);

        tb1.getBundleContext().registerService(DTO.class, new TestDTO(), properties);
        Collection<ServiceReference<DTO>> refs = getContext().getServiceReferences(DTO.class, "(dtotest=" + getName() + ")");
        assertEquals("Wrong number of ServiceReferences", 1, refs.size());
        ServiceReference<DTO> ref = refs.iterator().next();
        getContext().getService(ref);
        assertNotNull("Null ServiceReference", ref);
        ServiceReferenceDTO[] dtos = tb1.adapt(ServiceReferenceDTO[].class);
        assertNotNull("ServiceReferenceDTO[] is null", dtos);
        assertEquals("ServiceReferenceDTO[] wrong length", 1, dtos.length);
        ServiceReferenceDTO dto = dtos[0];
        assertEquals("ServiceReferenceDTO has wrong bundle", tb1.getBundleId(), dto.bundle);
        assertServiceReferenceDTO(ref, dto);
        tb1.stop();
        dtos = tb1.adapt(ServiceReferenceDTO[].class);
		assertNull("ServiceReferenceDTO[] for stopped bundle is not null", dtos);
        tb1.uninstall();
        dtos = tb1.adapt(ServiceReferenceDTO[].class);
        assertNull("ServiceReferenceDTO[] for uninstalled bundle is not null", dtos);
    }

	public void testServiceReferenceDTO() throws Exception {
		tb1.start();
		Dictionary<String,Object> properties = new Hashtable<String,Object>();
		properties.put("dtotest", getName());
		Version versionProp = new Version("1.2.3.Q");
		properties.put("testVersion", versionProp);
		properties.put("testNumber", Double.valueOf(3.14));
		properties.put("testBoolean", Boolean.TRUE);
		properties.put("testCharacter", Character.valueOf('#'));
		TestDTO dtoProp = new TestDTO();
		dtoProp.field = "prop";
		properties.put("testDTO", dtoProp);
		Map<String,Object> mapProp = new HashMap<String,Object>();
		mapProp.put("mapProp", dtoProp);
		properties.put("testMap", mapProp);
		List<Object> listProp = new ArrayList<Object>();
		listProp.add(dtoProp);
		properties.put("testList", listProp);
		Set<Object> setProp = new HashSet<Object>();
		setProp.add(dtoProp);
		properties.put("testSet", setProp);
		Object[] objectArrayProp = new Object[] {
				dtoProp, versionProp
		};
		properties.put("testObjectArray", objectArrayProp);
		double[] doubleArrayProp = new double[] {
				3.14
		};
		properties.put("testdoubleArray", doubleArrayProp);
		Version[] versionArray = new Version[] {
				versionProp
		};
		properties.put("testVersionArray", versionArray);

		tb1.getBundleContext()
				.registerService(DTO.class, new TestDTO(), properties);
		Collection<ServiceReference<DTO>> refs = getContext()
				.getServiceReferences(DTO.class, "(dtotest=" + getName() + ")");
		assertEquals("Wrong number of ServiceReferences", 1, refs.size());
		ServiceReference<DTO> ref = refs.iterator().next();
		getContext().getService(ref);
		assertNotNull("Null ServiceReference", ref);
		ServiceReferenceDTO dto = ref.adapt(ServiceReferenceDTO.class);
		assertEquals("ServiceReferenceDTO has wrong bundle", tb1.getBundleId(),
				dto.bundle);
		assertServiceReferenceDTO(ref, dto);
		tb1.stop();
	}

    public void testFrameworkDTO() throws Exception {
        tb1.start();
        FrameworkDTO dto = tb1.adapt(FrameworkDTO.class);
        assertNull("FrameworkDTO is not null", dto);
        Bundle systemBundle = getContext().getBundle(0);
        dto = systemBundle.adapt(FrameworkDTO.class);
        assertNotNull("FrameworkDTO is null", dto);
        assertNotNull("FrameworkDTO bundles is null", dto.bundles);
        Bundle[] bundlesArray = systemBundle.getBundleContext().getBundles();
        Map<Long, Bundle> bundlesMap = new HashMap<Long, Bundle>(bundlesArray.length);
        for (Bundle b : bundlesArray) {
            bundlesMap.put(Long.valueOf(b.getBundleId()), b);
        }
        for (BundleDTO b : dto.bundles) {
            Bundle bundle = bundlesMap.remove(Long.valueOf(b.id));
            assertNotNull("FrameworkDTO bundles has wrong bundle", bundle);
            assertBundleDTO(bundle, b);
        }
        assertNotNull("FrameworkDTO has null properties", dto.properties);
        for (Map.Entry<String, Object> entry : dto.properties.entrySet()) {
            assertEquals("FrameworkDTO wrong property", systemBundle.getBundleContext().getProperty(entry.getKey()), entry.getValue());
        }
        assertNotNull("FrameworkDTO services is null", dto.services);
        ServiceReference<?>[] services = systemBundle.getBundleContext().getAllServiceReferences(null, null);
        if (services == null) {
            assertEquals("FrameworkDTO services not empty", 0, dto.services.size());
            return;
        }
        assertEquals("FrameworkDTO services wrong size", services.length, dto.services.size());
        Map<Long, ServiceReference<?>> servicesMap = new HashMap<Long, ServiceReference<?>>(services.length);
        for (ServiceReference<?> s : services) {
            servicesMap.put((Long) s.getProperty(Constants.SERVICE_ID), s);
        }
        for (ServiceReferenceDTO s : dto.services) {
            ServiceReference<?> service = servicesMap.remove(s.properties.get(Constants.SERVICE_ID));
            assertNotNull("FrameworkDTO services wrong", service);
            assertServiceReferenceDTO(service, s);
        }
    }

    private void assertServiceReferenceDTO(ServiceReference<?> ref, ServiceReferenceDTO dto) throws Exception {
		assertNotNull("dto is null", dto);
        assertEquals("ServiceReferenceDTO has wrong bundle", ref.getBundle().getBundleId(), dto.bundle);
        assertNotNull("ServiceReferenceDTO has null properties", dto.properties);
        String[] keys = ref.getPropertyKeys();
        assertEquals("ServiceReferenceDTO properties the wrong size", keys.length, dto.properties.size());
        for (String k : keys) {
			Object v = ref.getProperty(k);
			if (Constants.SERVICE_ID.equals(k)) {
				assertEquals("service.id does not match", ((Long) v).longValue(), dto.id);
			}
			assertValueEquals("ServiceReferenceDTO has wrong property", v, dto.properties.get(k));
        }
        assertNotNull("ServiceReferenceDTO has null usingBundles", dto.usingBundles);
        Bundle[] using = ref.getUsingBundles();
        if (using == null) {
            assertEquals("ServiceReferenceDTO usingBundles not empty", 0, dto.usingBundles.length);
            return;
        }
        assertEquals("ServiceReferenceDTO usingBundles wrong size", using.length, dto.usingBundles.length);
        Map<Long, Bundle> bundlesMap = new HashMap<Long, Bundle>(using.length);
        for (Bundle b : using) {
            bundlesMap.put(Long.valueOf(b.getBundleId()), b);
        }
        for (long id : dto.usingBundles) {
            Bundle bundle = bundlesMap.remove(Long.valueOf(id));
            assertNotNull("ServiceReferenceDTO usingBundles has wrong bundle", bundle);
        }
    }

    private void assertValueEquals(String message, Object expected, Object actual) throws Exception {
        if (expected.getClass().isArray()) {
            assertTrue(message, actual.getClass().isArray());
            final int length = Array.getLength(expected);
            assertEquals(message, length, Array.getLength(actual));
            for (int i = 0; i < length; i++) {
                assertValueEquals(message, Array.get(expected, i), Array.get(actual, i));
            }
            return;
        }
        assertFalse(message, actual.getClass().isArray());
        if (actual instanceof String && (!(expected instanceof String))) {
            expected = String.valueOf(expected);
        }
        assertEquals(message, expected, actual);
    }

    public void testFrameworkStartLevelDTO() throws Exception {
        tb1.start();
        FrameworkStartLevelDTO dto = tb1.adapt(FrameworkStartLevelDTO.class);
        assertNull("FrameworkStartLevelDTO is not null", dto);
        Bundle systemBundle = getContext().getBundle(0);
        dto = systemBundle.adapt(FrameworkStartLevelDTO.class);
        assertNotNull("FrameworkStartLevelDTO is null", dto);
        FrameworkStartLevel fsl = systemBundle.adapt(FrameworkStartLevel.class);
        assertNotNull("FrameworkStartLevel is null", fsl);
        assertEquals("FrameworkStartLevelDTO initialBundleStartLevel wrong", fsl.getInitialBundleStartLevel(), dto.initialBundleStartLevel);
        assertEquals("FrameworkStartLevelDTO startLevel wrong", fsl.getStartLevel(), dto.startLevel);
    }

    public static class TestDTO extends DTO {
        public String field;
    }

	private Set<BundleWiring> getAllWirings() {
		Set<Bundle> allBundles = new HashSet<>(
				Arrays.asList(getContext().getBundles()));
		FrameworkWiring fwkWiring = getContext()
				.getBundle(Constants.SYSTEM_BUNDLE_LOCATION)
				.adapt(FrameworkWiring.class);
		allBundles.addAll(fwkWiring.getRemovalPendingBundles());

		Set<BundleWiring> allWirings = new HashSet<>();
		for (Bundle bundle : allBundles) {
			BundleRevisions revisions = bundle.adapt(BundleRevisions.class);
			for (BundleRevision revision : revisions.getRevisions()) {
				BundleWiring wiring = revision.getWiring();
				if (wiring != null) {
					allWirings.add(wiring);
				}
			}
		}
		return allWirings;
	}

	public void testFrameworkWiringDTO() throws Exception {
		Bundle systemBundle = getContext().getBundle(0);

		tb1.start();
		tb2.start();
		int initialWiringCount = getAllWirings().size();

		Map<BundleRevision,Integer> revisionIds = getRevisionIDs(
				new HashMap<BundleRevision,Integer>());
		FrameworkWiringDTO dto = systemBundle.adapt(FrameworkWiringDTO.class);
		assertEquals("Wrong number of wirings", initialWiringCount,
				dto.wirings.size());
		assertFrameworkWiringDTO(getAllWirings(), dto, revisionIds);

		tb1.update(entryStream("dto.tb1.jar"));

		// get the latest revision ids now to pick up the new revision
		revisionIds = getRevisionIDs(revisionIds);

		dto = systemBundle.adapt(FrameworkWiringDTO.class);
		assertEquals("Wrong number of wirings", initialWiringCount + 1,
				dto.wirings.size());
		assertFrameworkWiringDTO(getAllWirings(), dto, revisionIds);

		Wiring.synchronousRefreshBundles(getContext(), tb1);

		dto = systemBundle.adapt(FrameworkWiringDTO.class);
		assertEquals("Wrong number of wirings", initialWiringCount,
				dto.wirings.size());
		assertFrameworkWiringDTO(getAllWirings(), dto, revisionIds);

		tb1.uninstall();

		assertEquals(Bundle.UNINSTALLED, tb1.getState());

		dto = systemBundle.adapt(FrameworkWiringDTO.class);
		assertEquals("Wrong number of wirings", initialWiringCount,
				dto.wirings.size());
		assertFrameworkWiringDTO(getAllWirings(), dto, revisionIds);

		// Refreshing will remove the removal pending wiring
		// The tb2 wiring will not exist because it cannot resolve
		Wiring.synchronousRefreshBundles(getContext(), tb1);
		dto = systemBundle.adapt(FrameworkWiringDTO.class);
		assertEquals("Wrong number of wirings", initialWiringCount - 2,
				dto.wirings.size());
		assertFrameworkWiringDTO(getAllWirings(), dto, revisionIds);
	}

	private Map<BundleRevision,Integer> getRevisionIDs(
			Map<BundleRevision,Integer> revisionIDs) {
		// Get every revision's DTO ID so we can easily look it up later
		// This must to be done while the bundles are installed
		for (Bundle bundle : getContext().getBundles()) {
			BundleRevisions revisions = bundle.adapt(BundleRevisions.class);
			List<BundleRevision> revisionList = revisions.getRevisions();
			BundleRevisionDTO[] revisionDTOs = bundle
					.adapt(BundleRevisionDTO[].class);
			assertEquals("Unexpected number of dtos",
					revisions.getRevisions().size(), revisionDTOs.length);
			for (int i = 0; i < revisionDTOs.length; i++) {
				revisionIDs.put(revisionList.get(i),
						Integer.valueOf(revisionDTOs[i].id));
			}
		}
		return revisionIDs;
	}
}

/*
 * Copyright (c) OSGi Alliance (2012, 2013). All Rights Reserved.
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
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.osgi.dto.DTO;
import org.osgi.dto.framework.BundleDTO;
import org.osgi.dto.framework.FrameworkDTO;
import org.osgi.dto.framework.ServiceReferenceDTO;
import org.osgi.dto.framework.startlevel.BundleStartLevelDTO;
import org.osgi.dto.framework.startlevel.FrameworkStartLevelDTO;
import org.osgi.dto.framework.wiring.BundleRevisionDTO;
import org.osgi.dto.framework.wiring.BundleRevisionsDTO;
import org.osgi.dto.framework.wiring.BundleWireDTO;
import org.osgi.dto.framework.wiring.BundleWiringDTO;
import org.osgi.dto.framework.wiring.BundleWiringsDTO;
import org.osgi.dto.resource.CapabilityDTO;
import org.osgi.dto.resource.RequirementDTO;
import org.osgi.dto.resource.WireDTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.test.support.OSGiTestCase;

public class DTOTestCase extends OSGiTestCase {

    private Bundle        tb1;
    private Map<DTO, DTO> objects;

    protected void setUp() throws Exception {
        tb1 = install("dto.tb1.jar");
        objects = new IdentityHashMap<DTO, DTO>();
    }

    protected void tearDown() throws Exception {
        if ((tb1.getState() & Bundle.UNINSTALLED) != Bundle.UNINSTALLED) {
            tb1.uninstall();
        }
    }

    private boolean dtoUnderTest(DTO dto) throws Exception {
        assertNotNull("dto is null", dto);
        boolean underTest = objects.put(dto, dto) != null;
        return underTest;
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
        if (dtoUnderTest(dto)) {
            return;
        }
        assertEquals("Bundle id does not match", bundle.getBundleId(), dto.id);
        assertEquals("Bundle lastModified does not match", bundle.getLastModified(), dto.lastModified);
        assertEquals("Bundle state does not match", bundle.getState(), dto.state);
        assertEquals("Bundle state does not match", bundle.getState(), dto.state);
        assertEquals("Bundle symbolicName does not match", bundle.getSymbolicName(), dto.symbolicName);
        assertEquals("Bundle version does not match", bundle.getVersion(), Version.parseVersion(dto.version));
    }

    public void testBundleStartLevelDTO() throws Exception {
        BundleStartLevel bsl = tb1.adapt(BundleStartLevel.class);
        BundleStartLevelDTO dto = tb1.adapt(BundleStartLevelDTO.class);
        assertBundleStartLevelDTO(bsl, dto);
        bsl.setStartLevel(10);
        tb1.start(Bundle.START_ACTIVATION_POLICY);
        assertFalse("Bundle startLevel matches", bsl.getStartLevel() == dto.startLevel);
        assertFalse("Bundle activationPolicyUsed matches", bsl.isActivationPolicyUsed() == dto.activationPolicyUsed);
        assertFalse("Bundle persistentlyStarted matches", bsl.isPersistentlyStarted() == dto.persistentlyStarted);
        dto = tb1.adapt(BundleStartLevelDTO.class);
        assertBundleStartLevelDTO(bsl, dto);

    }

    private void assertBundleStartLevelDTO(BundleStartLevel bsl, BundleStartLevelDTO dto) throws Exception {
        if (dtoUnderTest(dto)) {
            return;
        }
        assertEquals("Bundle startLevel does not match", bsl.getStartLevel(), dto.startLevel);
        assertEquals("Bundle activationPolicyUsed does not match", bsl.isActivationPolicyUsed(), dto.activationPolicyUsed);
        assertEquals("Bundle persistentlyStarted does not match", bsl.isPersistentlyStarted(), dto.persistentlyStarted);
    }

    public void testBundleRevisionDTO() throws Exception {
        tb1.start();
        BundleRevision revision = tb1.adapt(BundleRevision.class);
        BundleRevisionDTO dto = tb1.adapt(BundleRevisionDTO.class);
        assertBundleRevisionDTO(revision, dto);
        tb1.uninstall();
        assertEquals(Bundle.UNINSTALLED, tb1.getState());
        dto = tb1.adapt(BundleRevisionDTO.class);
        assertNull("Current BundleRevisionDTO for uninstalled bundle is not null", dto);
    }

    private void assertBundleRevisionDTO(BundleRevision revision, BundleRevisionDTO dto) throws Exception {
        if (dtoUnderTest(dto)) {
            return;
        }
        assertBundleDTO(revision.getBundle(), dto.bundle);
        assertEquals("BundleRevision symbolicName does not match", revision.getSymbolicName(), dto.symbolicName);
        assertEquals("BundleRevision version does not match", revision.getVersion(), Version.parseVersion(dto.version));
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
        if (dtoUnderTest(dto)) {
            return;
        }
        assertBundleRevisionDTO(cap.getResource(), (BundleRevisionDTO) dto.resource);
        assertEquals("BundleCapability namespace does not match", cap.getNamespace(), dto.namespace);
		assertAttributesDTO("BundleCapability attributes does not match", cap.getAttributes(), dto.attributes);
        assertEquals("BundleCapability directives does not match", cap.getDirectives(), dto.directives);
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
        if (dtoUnderTest(dto)) {
            return;
        }
        assertBundleRevisionDTO(req.getResource(), (BundleRevisionDTO) dto.resource);
        assertEquals("BundleRequirement namespace does not match", req.getNamespace(), dto.namespace);
		assertAttributesDTO("BundleRequirement attributes does not match", req.getAttributes(), dto.attributes);
        assertEquals("BundleRequirement directives does not match", req.getDirectives(), dto.directives);
    }

    public void testBundleRevisionsDTO() throws Exception {
        tb1.start();
        BundleRevisions revisions = tb1.adapt(BundleRevisions.class);
        BundleRevisionsDTO dto = tb1.adapt(BundleRevisionsDTO.class);
        assertBundleRevisionsDTO(revisions, dto);
        tb1.uninstall();
        assertEquals(Bundle.UNINSTALLED, tb1.getState());
        dto = tb1.adapt(BundleRevisionsDTO.class);
        assertNotNull("Current BundleRevisionsDTO for uninstalled bundle is null", dto);
        assertEquals("Current BundleRevisionsDTO for uninstalled bundle is not empty", 0, dto.revisions.size());
    }

    private void assertBundleRevisionsDTO(BundleRevisions revisions, BundleRevisionsDTO dto) throws Exception {
        if (dtoUnderTest(dto)) {
            return;
        }
        List<BundleRevision> revs = revisions.getRevisions();
        List<BundleRevisionDTO> dtos = dto.revisions;
        final int size = revs.size();
        assertEquals("Revision count does not match", size, dtos.size());
        for (int i = 0; i < size; i++) {
            assertBundleRevisionDTO(revs.get(i), dtos.get(i));
        }
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
        if (dtoUnderTest(dto)) {
            return;
        }
        assertEquals("BundleWiring isCurrent does not match", wiring.isCurrent(), dto.current);
        assertEquals("BundleWiring isCurrent does not match", wiring.isInUse(), dto.inUse);
        assertBundleRevisionDTO(wiring.getRevision(), (BundleRevisionDTO) dto.resource);
        assertListCapabilityDTO(wiring.getCapabilities(null), dto.capabilities);
        assertListRequirementDTO(wiring.getRequirements(null), dto.requirements);
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
        if (dtoUnderTest(dto)) {
            return;
        }
        assertBundleWiringDTO(wire.getProviderWiring(), dto.providerWiring);
        assertBundleWiringDTO(wire.getRequirerWiring(), dto.requirerWiring);
        assertCapabilityDTO(wire.getCapability(), dto.capability);
        assertRequirementDTO(wire.getRequirement(), dto.requirement);
        assertBundleRevisionDTO(wire.getProvider(), (BundleRevisionDTO) dto.provider);
        assertBundleRevisionDTO(wire.getRequirer(), (BundleRevisionDTO) dto.requirer);
    }

    public void testBundleWiringsDTO() throws Exception {
        tb1.start();
        BundleRevisions revisions = tb1.adapt(BundleRevisions.class);
        List<BundleRevision> revs = revisions.getRevisions();
        final int size = revs.size();
        List<BundleWiring> wirings = new ArrayList<BundleWiring>(size);
        for (int i = 0; i < size; i++) {
            wirings.add(revs.get(i).getWiring());
        }
        BundleWiringsDTO dto = tb1.adapt(BundleWiringsDTO.class);
        assertBundleWiringsDTO(wirings, dto);
        tb1.uninstall();
        assertEquals(Bundle.UNINSTALLED, tb1.getState());
        dto = tb1.adapt(BundleWiringsDTO.class);
        assertNotNull("Current BundleWiringsDTO for uninstalled bundle is null", dto);
        assertEquals("Current BundleWiringsDTO for uninstalled bundle is not empty", 0, dto.wirings.size());
    }

    private void assertBundleWiringsDTO(List<BundleWiring> wirings, BundleWiringsDTO dto) throws Exception {
        if (dtoUnderTest(dto)) {
            return;
        }
        List<BundleWiringDTO> dtos = dto.wirings;
        final int size = wirings.size();
        assertEquals("Wiring count does not match", size, dtos.size());
        for (int i = 0; i < size; i++) {
            assertBundleWiringDTO(wirings.get(i), dtos.get(i));
        }
    }

    public void testArrayServiceReferenceDTO() throws Exception {
        tb1.start();
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("dtotest", getName());
        Version versionProp = new Version("1.2.3.Q");
        properties.put("testVersion", versionProp);
        properties.put("testNumber", new Double(3.14));
        properties.put("testBoolean", Boolean.TRUE);
        properties.put("testCharacter", new Character('#'));
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
        assertNotNull("ServiceReferenceDTO[] for stopped bundle is null", dtos);
        assertEquals("ServiceReferenceDTO[] for stopped bundle is not empty ", 0, dtos.length);
        tb1.uninstall();
        dtos = tb1.adapt(ServiceReferenceDTO[].class);
        assertNull("ServiceReferenceDTO[] for uninstalled bundle is not null", dtos);
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
            bundlesMap.put(new Long(b.getBundleId()), b);
        }
        for (BundleDTO b : dto.bundles) {
            Bundle bundle = bundlesMap.remove(new Long(b.id));
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
        if (dtoUnderTest(dto)) {
            return;
        }
        assertEquals("ServiceReferenceDTO has wrong bundle", ref.getBundle().getBundleId(), dto.bundle);
        assertNotNull("ServiceReferenceDTO has null properties", dto.properties);
        String[] keys = ref.getPropertyKeys();
        assertEquals("ServiceReferenceDTO properties the wrong size", keys.length, dto.properties.size());
        for (String k : keys) {
            assertValueEquals("ServiceReferenceDTO has wrong property", ref.getProperty(k), dto.properties.get(k));
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
            bundlesMap.put(new Long(b.getBundleId()), b);
        }
        for (long id : dto.usingBundles) {
            Bundle bundle = bundlesMap.remove(new Long(id));
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

    public void testDTOtoString() throws Exception {
        TestDTO dto = new TestDTO();
        dto.field = "testValue";
        String result = dto.toString();
        assertTrue("toString does not include field name", result.indexOf("field") >= 0);
        assertTrue("toString does not include field value", result.indexOf("testValue") >= 0);
    }

    public static class TestDTO extends DTO {
        public String field;
    }
}

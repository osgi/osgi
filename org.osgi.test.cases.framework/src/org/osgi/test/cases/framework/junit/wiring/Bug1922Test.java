package org.osgi.test.cases.framework.junit.wiring;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

/**
 * This test implements the conclusions of CPEG Bug 1922.
 * <p/>
 * <ul>
 * <li>Attributes declared on {@link Constants#REQUIRE_CAPABILITY} must be 
 *     visible in {@link BundleRequirement#getAttributes()}.</li>
 * <li>Attributes declared on requirements within the osgi.wiring.* namespace
 *     must not be visible in {@link BundleRequirement#getAttributes()}.</li>
 * <li>Attributes declared on requirements within the osgi.wiring.* namespace
 *     must be used to generate a {@link Constants#FILTER_DIRECTIVE} visible in
 *     {@link BundleRequirement#getDirectives()}. Any existing {@link
 *     Constants#FILTER_DIRECTIVE} must be overwritten.</li>
 * <li>Directives declared on {@link Constants#REQUIRE_CAPABILITY} must be
 *     visible in {@link BundleRequirement#getDirectives()}.
 * <li>Directives declared within the osgi.wiring.* namespace must be visible
 *     in {@link BundleRequirement#getDirectives()}.</li>
 * </ul>
 * @see https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1922
 */
public class Bug1922Test extends WiringTest {
	private BundleRequirement bundleRequirement;
	private BundleRequirement hostRequirement;
	private BundleRequirement packageRequirement;
	private BundleRequirement requirement;
	
	public void testRequireCapabilityRequirementAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("foo", "bar");
		assertEquals("Requirement attributes must match Require-Capability header", attributes, requirement.getAttributes());
	}
	
	public void testOsgiWiringRequirementAttributes() {
		assertEmptyAttributes(bundleRequirement.getAttributes());
		assertEmptyAttributes(hostRequirement.getAttributes());
		assertEmptyAttributes(packageRequirement.getAttributes());
	}
	
	public void testOsgiWiringRequirementFilter() {
		String filter = bundleRequirement.getDirectives().get(Constants.FILTER_DIRECTIVE);
		assertFilter(filter);
		assertFalse("Declared filters in osgi.wiring.* namespace must be overwritten", filter.equals("(foo=bar)"));
		assertAttributeInFilter("foo=bar", filter);
		assertAttributeInFilter("bundle-version", filter);
		filter = hostRequirement.getDirectives().get(Constants.FILTER_DIRECTIVE);
		assertFilter(filter);
		assertAttributeInFilter("foo=bar", filter);
		assertAttributeInFilter("bundle-version", filter);
		filter = packageRequirement.getDirectives().get(Constants.FILTER_DIRECTIVE);
		assertFilter(filter);
		assertAttributeInFilter("version", filter);
		assertAttributeInFilter("specification-version", filter);
		assertAttributeInFilter("bundle-symbolic-name=org.osgi.test.cases.framework.wiring.1922.frag", filter);
		assertAttributeInFilter("bundle-version", filter);
		assertAttributeInFilter("foo=bar", filter);
	}
	
	public void testRequireCapabilityRequirementDirectives() {
		assertDirective(Constants.FILTER_DIRECTIVE, "(foo=bar)", requirement);
		assertDirective("foo", "bar", requirement);
	}
	
	public void testOsgiWiringRequirementDirectives() {
		assertDirective("visibility", "reexport", bundleRequirement);
		assertDirective("resolution", "optional", bundleRequirement);
		assertDirective("foo", "bar", bundleRequirement);
		assertDirective("foo", "bar", hostRequirement);
		assertDirective("resolution", "optional", packageRequirement);
		assertDirective("foo", "bar", packageRequirement);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Bundle tb1 = install("wiring.1922.jar");
		Bundle tb1Frag = install("wiring.1922.frag.jar");
		assertTrue("Bundles should have resolved", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb1})));
		BundleRevision revision = tb1.adapt(BundleRevision.class);
		List<BundleRequirement> requirements = revision.getDeclaredRequirements(BundleRevision.BUNDLE_NAMESPACE);
		assertEquals("One Require-Bundle requirement should exist", 1, requirements.size());
		bundleRequirement = requirements.get(0);
		requirements = revision.getDeclaredRequirements(BundleRevision.PACKAGE_NAMESPACE);
		assertEquals("One Import-Package requirement should exist", 1, requirements.size());
		packageRequirement = requirements.get(0);
		requirements = revision.getDeclaredRequirements("org.osgi.test.cases.framework.wiring");
		assertEquals("One Require-Capability requirement should exist", 1, requirements.size());
		requirement = requirements.get(0);
		revision = tb1Frag.adapt(BundleRevision.class);
		requirements = revision.getDeclaredRequirements(BundleRevision.HOST_NAMESPACE);
		assertEquals("One Fragment-Host requirement should exist", 1, requirements.size());
		hostRequirement = requirements.get(0);
	}
	
	private void assertAttributeInFilter(String attribute, String filter) {
		assertTrue("Missing attribute in filter", filter.indexOf(attribute) != -1);
	}
	
	private void assertDirective(String name, String value, BundleRequirement requirement) {
		assertEquals("Missing directive or wrong value", value, requirement.getDirectives().get(name));
	}
	
	private void assertEmptyAttributes(Map<String, Object> attributes) {
		assertEquals("Requirement attributes must be empty for osgi.wiring.* namespace", 0, attributes.size());
	}
	
	private void assertFilter(String filter) {
		assertNotNull("Requirements in the osgi.wiring.* namespace must have a filter directive generated from the attributes", filter);
		try {
			FrameworkUtil.createFilter(filter);
		}
		catch (InvalidSyntaxException e) {
			fail("The generated filter string must be valid", e);
		}
	}
}

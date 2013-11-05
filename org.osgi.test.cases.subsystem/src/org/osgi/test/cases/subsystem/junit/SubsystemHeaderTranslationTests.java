package org.osgi.test.cases.subsystem.junit;

import java.util.Locale;

import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;
import org.osgi.test.cases.subsystem.util.SubsystemBuilder;

public class SubsystemHeaderTranslationTests extends SubsystemTest {
	public void testLocalizationOfUserDefinedHeaders() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME,getName())
						.header("User-Defined", "%user")
						.translation("subsystem", "user", "A user defined header value.").build());
		try {
			assertEquals("Wrong value", "A user defined header value.", s.getSubsystemHeaders(Locale.ENGLISH).get("User-Defined"));
		} finally {
			s.uninstall();
		}
	}
	
	public void testLocatingLocalizationEntriesByExactLocale() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_NAME, "%x")
						.translation("subsystem", "x", "default")
						.translation("subsystem_ab", "x", "ab")
						.translation("subsystem_ab_CD", "x", "ab_CD")
						.translation("subsystem_ab_EF", "x", "ab_EF")
						.translation("subsystem_ab_CD_YZ", "x", "ab_CD_YZ")
						.build());
		try {
			assertEquals("Wrong value", "%x", s.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_NAME));
			Locale locale = new Locale("mn");
			assertEquals("Wrong value", "default", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_NAME));
			locale = new Locale("ab");
			assertEquals("Wrong value", "ab", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_NAME));
			locale = new Locale("ab", "CD");
			assertEquals("Wrong value", "ab_CD", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_NAME));
			locale = new Locale("ab", "EF");
			assertEquals("Wrong value", "ab_EF", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_NAME));
			locale = new Locale("ab", "CD", "YZ");
			assertEquals("Wrong value", "ab_CD_YZ", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_NAME));
		} finally {
			s.uninstall();
		}
	}
	
	public void testLocatingLocalizationEntriesByInferredLocale() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_NAME, "%1")
						.header(SubsystemConstants.SUBSYSTEM_DESCRIPTION, "%2")
						.header(SubsystemConstants.SUBSYSTEM_CATEGORY, "%3")
						.header(SubsystemConstants.SUBSYSTEM_LICENSE, "%4")
						.translation("subsystem", "1", "default")
						.translation("subsystem_ab", "2", "ab")
						.translation("subsystem_ab_CD", "3", "ab_CD")
						.translation("subsystem_ab_CD_YZ", "4", "ab_CD_YZ")
						.build());
		try {
			Locale locale = new Locale("ab", "CD", "YZ");
			assertEquals("Wrong value", "default", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_NAME));
			assertEquals("Wrong value", "ab", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_DESCRIPTION));
			assertEquals("Wrong value", "ab_CD", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_CATEGORY));
			assertEquals("Wrong value", "ab_CD_YZ", s.getSubsystemHeaders(locale).get(SubsystemConstants.SUBSYSTEM_LICENSE));
		} finally {
			s.uninstall();
		}
	}

	public void testNonLocalizedValueUsedWhenHeaderHasSubsystemSemantics() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = null;
		try {
			s = root.install(
					getName(),
					new SubsystemBuilder()
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_VERSION, "%version")
							.translation("subsystem", "version", "1.0.0")
							.build());
			fail("Must use non-localized values of headers that have subsystem semantics");
		} catch (SubsystemException e) {
			// Okay.
		} finally {
			if (s != null)
				s.uninstall();
		}
	}

	public void testNullLocaleReturnsNonLocalizedValue() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_NAME, "%name")
						.translation("subsystem", "name", "A Name").build());
		try {
			assertEquals("Wrong value", "%name", s.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_NAME));
		} finally {
			s.uninstall();
		}
	}

	public void testSpacesInLocalizationKeys() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_NAME, "%subsystem name")
						.translation("subsystem", "subsystem name", "A Subsystem Name").build());
		try {
			assertEquals(
					"Wrong value",
					"A Subsystem Name",
					s.getSubsystemHeaders(Locale.ENGLISH).get(SubsystemConstants.SUBSYSTEM_NAME));
		} finally {
			s.uninstall();
		}
	}

	public void testSubsystemLocalizationHeaderPresenceWhenSpecifiedWithDefaultValue() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.build());
		try {
			String value = s.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_LOCALIZATION);
			assertTrue(
					"Wrong value",
					value == null || value.equals(SubsystemConstants.SUBSYSTEM_LOCALIZATION_DEFAULT_BASENAME));
		} finally {
			s.uninstall();
		}
	}

	public void testSubsystemLocalizationHeaderPresenceWhenSpecifiedWithNonDefaultValue() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_LOCALIZATION, "foo").build());
		try {
			assertEquals(
					"Wrong value",
					"foo",
					s.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_LOCALIZATION));
		} finally {
			s.uninstall();
		}
	}

	public void testSubsystemLocalizationHeaderPresenceWhenUnspecified() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder().header(
						SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.build());
		try {
			String value = s.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_LOCALIZATION);
			assertTrue(
					"Wrong value",
					value == null || value.equals(SubsystemConstants.SUBSYSTEM_LOCALIZATION_DEFAULT_BASENAME));
		} finally {
			s.uninstall();
		}
	}

	public void testTranslationWithoutSubsystemLocalizationHeader() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME,getName())
						.header(SubsystemConstants.SUBSYSTEM_NAME, "%name")
						.translation("subsystem", "name", "A Name")
						.build());
		try {
			assertEquals(
					"Wrong value",
					"A Name",
					s.getSubsystemHeaders(Locale.ENGLISH).get(SubsystemConstants.SUBSYSTEM_NAME));
		} finally {
			s.uninstall();
		}
	}

	public void testTranslationWithSubsystemLocalizationHeaderNonDefault() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_CATEGORY, "%category")
						.header(SubsystemConstants.SUBSYSTEM_LOCALIZATION,
								"resources")
						.translation("resources", "category", "finance")
						.build());
		try {
			assertEquals(
					"Wrong value",
					"finance",
					s.getSubsystemHeaders(Locale.ENGLISH).get(SubsystemConstants.SUBSYSTEM_CATEGORY));
		} finally {
			s.uninstall();
		}
	}

	public void testTranslationWithSubsystemLocalizationHeaderDefault() throws Exception {
		Subsystem root = getRootSubsystem();
		Subsystem s = root.install(
				getName(),
				new SubsystemBuilder()
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_DESCRIPTION, "%description")
						.header(SubsystemConstants.SUBSYSTEM_LOCALIZATION, SubsystemConstants.SUBSYSTEM_LOCALIZATION_DEFAULT_BASENAME)
						.translation("subsystem", "description", "An empty subsystem.")
						.build());
		try {
			assertEquals(
					"Wrong value",
					"An empty subsystem.",
					s.getSubsystemHeaders(Locale.ENGLISH).get(SubsystemConstants.SUBSYSTEM_DESCRIPTION));
		} finally {
			s.uninstall();
		}
	}
}

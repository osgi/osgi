package org.osgi.test.cases.feature.assertj;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureConfiguration;
import org.osgi.service.feature.ID;

class Predicates {

	static boolean matches(Optional<String> string, String match) {
		if (string.isPresent()) {
			return string.get().matches(match);
		}
		return false;
	}

	static boolean is(Optional<String> string, String compare) {
		if (string.isPresent()) {
			return Objects.equals(string.get(), compare);
		}
		return false;
	}

	public static Predicate<Feature> nameMatches(String pattern) {
		return (f) -> matches(f.getName(), pattern);
	}

	public static Predicate<Feature> name(String name) {
		return (f) -> is(f.getName(), name);
	}

	public static Predicate<Feature> descriptionMatches(String pattern) {
		return (f) -> matches(f.getDescription(), pattern);

	}

	public static Predicate<Feature> description(String description) {
		return (f) -> is(f.getDescription(), description);
	}

	public static Predicate<Feature> docURLMatches(String pattern) {
		return (f) -> matches(f.getDocURL(), pattern);

	}

	public static Predicate<Feature> docURL(String docURL) {
		return (f) -> is(f.getDocURL(), docURL);
	}

	public static Predicate<Feature> licenseMatches(String pattern) {
		return (f) -> matches(f.getLicense(), pattern);

	}

	public static Predicate<Feature> license(String license) {
		return (f) -> is(f.getLicense(), license);
	}


	public static Predicate<Feature> vendorMatches(String pattern) {
		return (f) -> matches(f.getVendor(), pattern);

	}

	public static Predicate<Feature> vendor(String vendor) {
		return (f) -> is(f.getVendor(), vendor);
	}

	public static Predicate<Feature> copyrightMatches(String pattern) {
		return (f) -> matches(f.getCopyright(), pattern);

	}

	public static Predicate<Feature> copyright(String copyright) {
		return (f) -> is(f.getCopyright(), copyright);
	}

	public static Predicate<Feature> scmMatches(String pattern) {
		return (f) -> matches(f.getSCM(), pattern);

	}

	public static Predicate<Feature> scm(String scm) {
		return (f) -> is(f.getSCM(), scm);
	}

	static interface IDPredicates {
		public static Predicate<ID> typeMatches(String pattern) {
			return (id) -> matches(id.getType(), pattern);

		}

		public static Predicate<ID> type(String type) {
			return (id) -> is(id.getType(), type);
		}

		public static Predicate<ID> classifierMatches(String pattern) {
			return (id) -> matches(id.getClassifier(), pattern);

		}

		public static Predicate<ID> classifier(String classifier) {
			return (id) -> is(id.getClassifier(), classifier);
		}
	}

	static interface ConfigurationsPredicates {
		public static Predicate<FeatureConfiguration> factoryPidMatches(
				String pattern) {
			return (fc) -> matches(fc.getFactoryPid(), pattern);

		}

		public static Predicate<FeatureConfiguration> factoryPid(
				String factoryPid) {
			return (fc) -> is(fc.getFactoryPid(), factoryPid);
		}
	}

}
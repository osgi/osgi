package org.osgi.test.cases.feature.junit;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertThrows;
import static org.osgi.test.cases.feature.assertj.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.json.spi.JsonProvider;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.feature.BuilderFactory;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureBuilder;
import org.osgi.service.feature.FeatureService;
import org.osgi.test.cases.feature.assertj.FeatureAssert;
import org.osgi.test.cases.feature.assertj.FeatureConfigurationAssert;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class FeatureServiceTest {

	@InjectService(timeout = 200)
	FeatureService	fs;

	@InjectService(timeout = 200)
	JsonProvider	jsonP;

	@Test
	void featureService_isNotNull() throws Exception {
		Assertions.assertThat(fs).isNotNull();
	}

	@Nested
	class BuilderFactoryTest {

		@Test
		void getBuilderFactory_isNotNull() throws Exception {
			BuilderFactory builderFactory = fs.getBuilderFactory();
			Assertions.assertThat(builderFactory).isNotNull();
		}
	}

	@Nested
	class FeatureConfigurationBuilderTest {

		@Test
		void build_pid_ok() {

			FeatureConfigurationAssert
					.assertThat(fs.getBuilderFactory()
							.newConfigurationBuilder("foo")
							.build())
					.isNotNull()
					.hasPid("foo")
					.hasValuesThat()
					.isNotNull()
					.isEmpty();

		}

		@Test
		void build_factoryPid_ok() {

			FeatureConfigurationAssert
					.assertThat(fs.getBuilderFactory()
							.newConfigurationBuilder("foo", "bar")
							.build())
					.isNotNull()
					.hasPid("foo~bar")
					.hasFactoryPid("foo")
					.hasValuesThat()
					.isNotNull()
					.isEmpty();
		}
	}

	@Nested
	class FeatureArtifactBuilderTest {

		@Test
		void addMetadata_ok() throws Exception {

			fs.getBuilderFactory()
					.newArtifactBuilder(fs.getID("g", "a", "v"))
					.addMetadata("int", 0)
					.addMetadata("Integer", Integer.valueOf(0))
					.addMetadata("d", 2.2)
					.addMetadata("bd", BigDecimal.TEN)
					.build();

		}

		@Test
		void addMetadataMap_ok() throws Exception {

			Map<String,Object> map = new HashMap<>();
			map.put("int", 0);
			map.put("Integer", Integer.valueOf(0));
			map.put("d", 2.2);
			map.put("bd", BigDecimal.TEN);
			fs.getBuilderFactory()
					.newArtifactBuilder(fs.getID("g", "a", "v"))
					.addMetadata(map)
					.build();

		}

		@Test
		void addMetadata_nullKey_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(null, "value")
						.build();
			});
		}

		@Test
		void addMetadata_EmptyKey_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata("", "value")
						.build();
			});
		}

		@Test
		void addMetadata_ComplexValue_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata("ref", new BundleDTO())
						.build();
			});

		}

		@Test
		void addMetadataMap_null_OK() throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(null)
						.build();
			});

		}

		@Test
		void addMetadataMap_emptyKeyMap_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap("", ""))
						.build();
			});
		}

		@ParameterizedTest
		@ValueSource(strings = {
				"id", "Id", "iD", "ID"
		})
		void addMetadataMap_idKeyMap_must_fail(String id) throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap(id, "foo"))
						.build();
			});
		}

		@Test
		void addMetadataMap_ComplexValueMap_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap("foo", new BundleDTO()))
						.build();
			});
		}

		@Test
		void addMetadataMap_nullkexMap_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap(null, "blah"))
						.build();
			});
		}

		@ParameterizedTest
		@ValueSource(strings = {
				"id", "Id", "iD", "ID"
		})
		void addMetadata_id_must_fail(String id) throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(id, "value");
			});
		}

		// TODO: when should the exception raise
		@ParameterizedTest
		@ValueSource(strings = {
				"id", "Id", "iD", "ID"
		})
		void build_fail_when_metadata_id(String id) throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(id, "value")
						.build();
			});

		}
	}

	@Nested
	class FeatureBundleBuilderTest {

		@Test
		void addMetadata_ok() throws Exception {

			fs.getBuilderFactory()
					.newArtifactBuilder(fs.getID("g", "a", "v"))
					.addMetadata("int", 0)
					.addMetadata("Integer", Integer.valueOf(0))
					.addMetadata("d", 2.2)
					.addMetadata("bd", BigDecimal.TEN)
					.build();

		}

		@Test
		void addMetadataMap_ok() throws Exception {

			Map<String,Object> map = new HashMap<>();
			map.put("int", 0);
			map.put("Integer", Integer.valueOf(0));
			map.put("d", 2.2);
			map.put("bd", BigDecimal.TEN);
			fs.getBuilderFactory()
					.newArtifactBuilder(fs.getID("g", "a", "v"))
					.addMetadata(map)
					.build();

		}

		@Test
		void addMetadata_nullKey_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(null, "value")
						.build();
			});
		}

		@Test
		void addMetadata_EmptyKey_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata("", "value")
						.build();
			});
		}

		@Test
		void addMetadata_ComplexValue_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata("ref", new BundleDTO())
						.build();
			});

		}

		@Test
		void addMetadataMap_null_OK() throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(null)
						.build();
			});

		}

		@Test
		void addMetadataMap_emptyKeyMap_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap("", ""))
						.build();
			});
		}

		@ParameterizedTest
		@ValueSource(strings = {
				"id", "Id", "iD", "ID"
		})
		void addMetadataMap_idKeyMap_must_fail(String id) throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap(id, "foo"))
						.build();
			});
		}

		@Test
		void addMetadataMap_ComplexValueMap_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap("foo", new BundleDTO()))
						.build();
			});
		}

		@Test
		void addMetadataMap_nullkexMap_must_fail() throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(singletonMap(null, "blah"))
						.build();
			});
		}

		@ParameterizedTest
		@ValueSource(strings = {
				"id", "Id", "iD", "ID"
		})
		void addMetadata_id_must_fail(String id) throws Exception {

			Assertions.assertThatThrownBy(() -> {

				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(id, "value");
			});
		}

		// TODO: when should the exception raise
		@ParameterizedTest
		@ValueSource(strings = {
				"id", "Id", "iD", "ID"
		})
		void build_fail_when_metadata_id(String id) throws Exception {

			Assertions.assertThatThrownBy(() -> {
				fs.getBuilderFactory()
						.newArtifactBuilder(fs.getID("g", "a", "v"))
						.addMetadata(id, "value")
						.build();
			});

		}
	}

	@Nested
	class ReadFeatureTest {

		@Nested
		class ReadFeatureIncompleteTest {
			@Test
			void null_shouldThrow() throws Exception {
				assertThrows(Throwable.class, () -> fs.readFeature(null));
			}

			@Test
			void empty_shouldThrow() throws Exception {
				assertThrows(Throwable.class,
						() -> fs.readFeature(new StringReader("")));
			}

			@Test
			void incomplete_id_shouldThrow() throws Exception {
				assertThrows(Throwable.class,
						() -> fs.readFeature(new StringReader("")));
			}
		}

		@Nested
		class ReadFeatureIDTest {

			@ParameterizedTest
			@ValueSource(strings = {
					":", "::", ":::", "::::", ":::::", //
					// " : ", " : : ", " : : : ", " : : : : ", " : : : : : ", //
					"g", "g:", "g::", "g:::", "g::::", //
					"g:a", "g:a:", "g:a::", "g:a:::", //
					"g:a:v:", "g:a:v::", //
					"g:a:t:c:", //
					"g:a:t::v", //
					"g::t:c:v", //
					"g:::t:v", //
					":a:t:c:v", //
					"g:a:t:c:v:", //
					"g:a:t:c:v:foo", //

			})
			void read_ID_must_fail(String valueID) throws Exception {

				StringWriter sw = new StringWriter();
				JSONWriter jsonWriter = new JSONWriter(sw);

				jsonWriter.object().key("id").value(valueID).endObject();

				assertThrows(Throwable.class,
						() -> fs.readFeature(new StringReader(sw.toString())));
			}

			@Test
			void read_ID_Full() throws Exception {
				StringWriter sw = new StringWriter();
				JSONWriter jsonWriter = new JSONWriter(sw);

				jsonWriter.object().key("id").value("g:a:t:c:v").endObject();
				Feature feature = fs
						.readFeature(new StringReader(sw.toString()));

				assertThat(feature).hasIDThat()
						.isEqualTo(fs.getID("g", "a", "v", "t", "c"));

			}
		}

		@Test
		void minimal_read() throws Exception {
			StringWriter sw = new StringWriter();
			JSONWriter jsonWriter = new JSONWriter(sw);

			jsonWriter.object().key("id").value("g:a:v").endObject();

			Feature feature = fs.readFeature(new StringReader(sw.toString()));

			FeatureAssert featureAssert = assertThat(feature);
			featureAssert.isNotNull()
					.hasDescription(null)
					.hasLicense(null)
					.hasName(null)
					.hasVendor(null)
					.isNotComplete();
			featureAssert.hasIDThat().isEqualTo(fs.getID("g", "a", "v"));
			featureAssert.hasConfigurationsThat().isEmpty();
			featureAssert.hasBundlesThat().isEmpty();
			featureAssert.hasExtensionsThat().isEmpty();
			featureAssert.hasVariablesThat().isEmpty();
		}

		// @Test
		// void read_config_null() throws Exception {
		// StringWriter sw = new StringWriter();
		// JSONWriter jsonWriter = new JSONWriter(sw);
		//
		// String pid = "factory.pid~name1";
		// jsonWriter.object()
		// .key("id")
		// .value("g:a:v")
		// .key("configurations")
		// .object()
		// .key(pid)
		// .value(null)
		// .endObject()
		// .endObject();
		//
		// Feature feature = fs.readFeature(new StringReader(sw.toString()));
		//
		// FeatureAssert featureAssert = assertThat(feature);
		// featureAssert.isNotNull()
		// .hasDescription(null)
		// .hasLicense(null)
		// .hasName(null)
		// .hasVendor(null)
		// .isNotComplete();
		// featureAssert.hasIDThat().isEqualTo(fs.getID("g", "a", "v"));
		// featureAssert.hasConfigurationThat(pid)
		// .hasFactoryPid("factory.pid")
		// .hasValuesThat()
		// .isEmpty();
		// }

		@Test
		void read_config_emptyMap() throws Exception {
			StringWriter sw = new StringWriter();
			JSONWriter jsonWriter = new JSONWriter(sw);

			String pid = "factory.pid~name1";
			jsonWriter.object()
					.key("id")
					.value("g:a:v")
					.key("configurations")
					.object()
					.key(pid)
					.value(Collections.emptyMap())
					.endObject()
					.endObject();

			Feature feature = fs.readFeature(new StringReader(sw.toString()));

			FeatureAssert featureAssert = assertThat(feature);
			featureAssert.isNotNull()
					.hasDescription(null)
					.hasLicense(null)
					.hasName(null)
					.hasVendor(null)
					.isNotComplete();
			featureAssert.hasIDThat().isEqualTo(fs.getID("g", "a", "v"));
			featureAssert.hasConfigurationThat(pid)
					.hasFactoryPid("factory.pid")
					.hasValuesThat()
					.isEmpty();

		}

		@Test
		void read_config_valuedMap() throws Exception {
			StringWriter sw = new StringWriter();
			JSONWriter jsonWriter = new JSONWriter(sw);

			String pid = "factory.pid~name1";
			jsonWriter.object()
					.key("id")
					.value("g:a:v")
					.key("configurations")
					.object()
					.key(pid)
					.value(Collections.singletonMap("k", "v"))
					.endObject()
					.endObject();

			Feature feature = fs.readFeature(new StringReader(sw.toString()));

			FeatureAssert featureAssert = assertThat(feature);
			featureAssert.isNotNull()
					.hasDescription(null)
					.hasLicense(null)
					.hasName(null)
					.hasVendor(null)
					.isNotComplete();
			featureAssert.hasIDThat().isEqualTo(fs.getID("g", "a", "v"));
			featureAssert.hasConfigurationThat(pid)
					.hasFactoryPid("factory.pid")
					.hasValuesThat()
					.containsOnly(new SimpleEntry<String,Object>("k", "v"));

		}

		@Test
		void read_config_FactoryPid() throws Exception {
			StringWriter sw = new StringWriter();
			JSONWriter jsonWriter = new JSONWriter(sw);

			String pid = "factory.pid~name1~name2";
			jsonWriter.object()
					.key("id")
					.value("g:a:v")
					.key("configurations")
					.object()
					.key(pid)
					.value(Collections.singletonMap("k", "v"))
					.endObject()
					.endObject();

			Feature feature = fs.readFeature(new StringReader(sw.toString()));

			FeatureAssert featureAssert = assertThat(feature);
			featureAssert.isNotNull()
					.hasDescription(null)
					.hasLicense(null)
					.hasName(null)
					.hasVendor(null)
					.isNotComplete();
			featureAssert.hasIDThat().isEqualTo(fs.getID("g", "a", "v"));

			featureAssert.hasConfigurationThat(pid)
					.hasFactoryPid("factory.pid");

		}

	}

	@Nested
	class WriteFeatureTest {
		@Test
		void null_shouldThrow() throws Exception {
			assertThrows(Throwable.class, () -> fs.writeFeature(null, null));

		}

		@SuppressWarnings("unchecked")
		@Test
		void write_ID_gav() throws Exception {
			FeatureBuilder featureBuilder = fs.getBuilderFactory()
					.newFeatureBuilder(fs.getID("g", "a", "v"));
			Feature feature = featureBuilder.build();

			JSONObject json = toJsonObject(fs, feature);

			Assertions.assertThat(json.keys()).toIterable().containsOnly("id");

			Object idJson = json.get("id");
			Assertions.assertThat(idJson).isNotNull().hasToString("g:a:v");

		}

		@SuppressWarnings("unchecked")
		@Test
		void write_ID_gavtc() throws Exception {
			FeatureBuilder featureBuilder = fs.getBuilderFactory()
					.newFeatureBuilder(fs.getID("g", "a", "v", "t", "c"));
			Feature feature = featureBuilder.build();

			JSONObject json = toJsonObject(fs, feature);

			Assertions.assertThat(json.keys()).toIterable().containsOnly("id");

			Object idJson = json.get("id");
			Assertions.assertThat(idJson).isNotNull().hasToString("g:a:t:c:v");

		}

		@SuppressWarnings("unchecked")
		@Test
		void write_ID_gavt() throws Exception {
			FeatureBuilder featureBuilder = fs.getBuilderFactory()
					.newFeatureBuilder(fs.getID("g", "a", "v", "t"));
			Feature feature = featureBuilder.build();

			JSONObject json = toJsonObject(fs, feature);

			Assertions.assertThat(json.keys()).toIterable().containsOnly("id");

			Object idJson = json.get("id");
			Assertions.assertThat(idJson).isNotNull().hasToString("g:a:t:v");

		}

	}

	JSONObject toJsonObject(FeatureService featureService, Feature feature)
			throws IOException, JSONException {
		Writer writer = new StringWriter();
		featureService.writeFeature(feature, writer);

		JSONObject jsonObject = new JSONObject(writer.toString());
		return jsonObject;
	}

}

package org.osgi.test.cases.feature.junit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.osgi.test.cases.feature.assertj.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.feature.BuilderFactory;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureBuilder;
import org.osgi.service.feature.FeatureBundle;
import org.osgi.service.feature.FeatureConfiguration;
import org.osgi.service.feature.FeatureExtension;
import org.osgi.service.feature.FeatureService;
import org.osgi.test.cases.feature.assertj.FeatureAssert;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class FeatureTest {

	@InjectService(timeout = 200)
	FeatureService fs;
	@Test
	void Feature_isNotNull() throws Exception {
		FeatureBuilder featureBuilder = createFeatureBuilderGAV(fs);
		Feature feature = featureBuilder.build();
		FeatureAssert featureAssert = assertThat(feature);
		featureAssert.isNotNull();
		featureAssert.hasConfigurationsThat().isNotNull().isEmpty();
		featureAssert.hasExtensionsThat().isNotNull().isEmpty();
		featureAssert.hasVariablesThat().isNotNull().isEmpty();
		featureAssert.hasBundlesThat().isNotNull().isEmpty();

	}

	@Test
	void Feature_isImmutable() throws Exception {
		FeatureBuilder featureBuilder = createFeatureBuilderGAV(fs);
		Feature feature = featureBuilder.build();

		// not sure that UnsupportedOperationException is thrown
		assertThrows(UnsupportedOperationException.class, () -> {
			feature.getBundles().add(mock(FeatureBundle.class));
		});

		assertThrows(UnsupportedOperationException.class, () -> {
			feature.getExtensions().put("foo", mock(FeatureExtension.class));
		});

		assertThrows(UnsupportedOperationException.class, () -> {
			feature.getConfigurations()
					.put("foo", mock(FeatureConfiguration.class));
		});

		assertThrows(UnsupportedOperationException.class, () -> {
			feature.getVariables().put("foo", "bar");
		});

	}

	private FeatureBuilder createFeatureBuilderGAV(
			FeatureService featureService) {
		BuilderFactory builderFactory = featureService.getBuilderFactory();
		FeatureBuilder featureBuilder = builderFactory
				.newFeatureBuilder(fs.getIDfromMavenCoordinates("g:a:v"));
		return featureBuilder;
	}

}

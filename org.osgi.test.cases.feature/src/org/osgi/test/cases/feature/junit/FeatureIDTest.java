package org.osgi.test.cases.feature.junit;

import static org.junit.Assert.assertThrows;
import static org.osgi.test.cases.feature.assertj.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.feature.FeatureService;
import org.osgi.service.feature.ID;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(ServiceExtension.class)
@ExtendWith(BundleContextExtension.class)
public class FeatureIDTest {

	// toString may use type and classifier
	// getType may return an Optional<String>
	// getClassifier may return an Optional<String>

	private static final String	G_A_V		= "g:a:v";
	private static final String	G_A_V_T		= G_A_V + ":t";
	private static final String	G_A_V_T_C	= G_A_V_T + ":c";

	@InjectService
	FeatureService				fs;

	@Test
	void getID3() throws Exception {

		ID id = fs.getID("g", "a", "v");
		assertThat(id).hasGroupId("g")
				.hasArtifactId("a")
				.hasVersion("v")
				.hasEmptyType()
				.hasEmptyClassifier()
				.hasToString(G_A_V);

	}

	@Test
	void constructor5_gav() throws Exception {

		ID id = fs.getID("g", "a", "v", null, null);
		assertThat(id).hasGroupId("g")
				.hasArtifactId("a")
				.hasVersion("v")
				.hasEmptyType()
				.hasEmptyClassifier()
				.hasToString(G_A_V);

	}

	@Test
	void getID5_gavt() throws Exception {

		ID id = fs.getID("g", "a", "v", "t", null);
		assertThat(id).hasGroupId("g")
				.hasArtifactId("a")
				.hasVersion("v")
				.hasType("t")
				.hasEmptyClassifier()
				.hasToString(G_A_V_T);
	}

	@Test
	void getID5_gavtc() throws Exception {

		ID id = fs.getID("g", "a", "v", "t", "c");
		assertThat(id).hasGroupId("g")
				.hasArtifactId("a")
				.hasVersion("v")
				.hasType("t")
				.hasClassifier("c")
				.hasToString(G_A_V_T_C);

	}

	@Test
	void getID3_shouldFail() throws Exception {

		assertThrows(Throwable.class, () -> fs.getID(null, null, null));
		assertThrows(Throwable.class, () -> fs.getID("", "", ""));
		assertThrows(Throwable.class, () -> fs.getID("g", "a", null));
		assertThrows(Throwable.class, () -> fs.getID("g", "a", ""));
		assertThrows(Throwable.class, () -> fs.getID("g", null, "v"));
		assertThrows(Throwable.class, () -> fs.getID("g", "", "v"));
		assertThrows(Throwable.class, () -> fs.getID(null, "a", "v"));
		assertThrows(Throwable.class, () -> fs.getID("", "a", "v"));

	}

	@Test
	void getID4_shouldFail() throws Exception {
		assertThrows(Throwable.class, () -> fs.getID(null, null, null, null));
		assertThrows(Throwable.class, () -> fs.getID("", "", "", ""));

		assertThrows(Throwable.class, () -> fs.getID("g", "a", "v", ""));
		assertThrows(Throwable.class, () -> fs.getID("g", "a", "v", null));

		assertThrows(Throwable.class, () -> fs.getID("g", "a", "", "t"));
		assertThrows(Throwable.class, () -> fs.getID("g", "a", null, "t"));

		assertThrows(Throwable.class, () -> fs.getID("g", "", "v", "t"));
		assertThrows(Throwable.class, () -> fs.getID("g", null, "v", "t"));

		assertThrows(Throwable.class, () -> fs.getID("", "a", "v", "t"));
		assertThrows(Throwable.class, () -> fs.getID(null, "a", "v", "t"));
	}

	@Test
	void getID5_shouldFail() throws Exception {
		assertThrows(Throwable.class,
				() -> fs.getID(null, null, null, null, null));
		assertThrows(Throwable.class, () -> fs.getID("", "", "", "", ""));

		assertThrows(Throwable.class, () -> fs.getID("g", "a", "v", "t", ""));
		assertThrows(Throwable.class, () -> fs.getID("g", "a", "v", "t", null));

		assertThrows(Throwable.class, () -> fs.getID("g", "a", "v", "", "c"));
		assertThrows(Throwable.class, () -> fs.getID("g", "a", "v", null, "c"));

		assertThrows(Throwable.class, () -> fs.getID("g", "a", "", "t", "c"));
		assertThrows(Throwable.class, () -> fs.getID("g", "a", null, "t", "c"));

		assertThrows(Throwable.class, () -> fs.getID("g", "", "v", "t", "c"));
		assertThrows(Throwable.class, () -> fs.getID("g", null, "v", "t", "c"));

		assertThrows(Throwable.class, () -> fs.getID("", "a", "v", "t", "c"));
		assertThrows(Throwable.class, () -> fs.getID(null, "a", "v", "t", "c"));

		assertThrows(Throwable.class,
				() -> fs.getID("g", "a", "v", null, null));

	}

	@Test
	void getIDX_shouldFail_NOT_SPECIFIED() throws Exception {

		assertThrows(Throwable.class,
				() -> fs.getID("g", "a", "v", "t", "c:c"));
		assertThrows(Throwable.class,
				() -> fs.getID("g", "a", "v", "t:t", "c"));
		assertThrows(Throwable.class,
				() -> fs.getID("g", "a", "v:v", "t", "c"));
		assertThrows(Throwable.class,
				() -> fs.getID("g", "a:a", "v", "t", "c"));
		assertThrows(Throwable.class,
				() -> fs.getID("g:g", "a", "v", "t", "c"));
		assertThrows(Throwable.class, () -> fs.getID(":", ":", ":", ":", ":"));

	}

	@Test
	void getIDfromMavenCoordinates_shouldFail() throws Exception {
		assertThrows(Throwable.class, () -> fs.getIDfromMavenCoordinates(null));
		assertThrows(Throwable.class, () -> fs.getIDfromMavenCoordinates(""));
		assertThrows(Throwable.class, () -> fs.getIDfromMavenCoordinates(":"));
		assertThrows(Throwable.class, () -> fs.getIDfromMavenCoordinates("::"));
		assertThrows(Throwable.class,
				() -> fs.getIDfromMavenCoordinates(":::"));
		assertThrows(Throwable.class,
				() -> fs.getIDfromMavenCoordinates("::::"));
		assertThrows(Throwable.class,
				() -> fs.getIDfromMavenCoordinates("g:a:v:"));
		assertThrows(Throwable.class,
				() -> fs.getIDfromMavenCoordinates("g:a:v:t:"));
		assertThrows(Throwable.class,
				() -> fs.getIDfromMavenCoordinates("g:a:v:t:c:"));
		assertThrows(Throwable.class,
				() -> fs.getIDfromMavenCoordinates("g:a:v:t:c:foo"));

	}

	@Test
	void getIDfromMavenCoordinates_Gav() throws Exception {
		ID id = fs.getIDfromMavenCoordinates(G_A_V);
		assertThat(id).hasGroupId("g")
				.hasArtifactId("a")
				.hasVersion("v")
				.hasEmptyType()
				.hasEmptyClassifier()
				.hasToString(G_A_V);

	}

	@Test
	void getIDfromMavenCoordinates_gavt() throws Exception {
		ID id = fs.getIDfromMavenCoordinates(G_A_V_T);
		assertThat(id).hasGroupId("g")
				.hasArtifactId("a")
				.hasVersion("v")
				.hasType("t")
				.hasEmptyClassifier()
				.hasToString(G_A_V_T);
	}

	@Test
	void getIDfromMavenCoordinates_gavtc() throws Exception {
		ID id = fs.getIDfromMavenCoordinates(G_A_V_T_C);
		assertThat(id).hasGroupId("g")
				.hasArtifactId("a")
				.hasVersion("v")
				.hasType("t")
				.hasClassifier("c")
				.hasToString(G_A_V_T_C);
	}
}
